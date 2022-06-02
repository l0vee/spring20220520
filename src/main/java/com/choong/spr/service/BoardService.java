package com.choong.spr.service;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.choong.spr.domain.BoardDto;
import com.choong.spr.mapper.BoardMapper;
import com.choong.spr.mapper.ReplyMapper;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
public class BoardService {

	@Autowired
	private BoardMapper mapper;
	
	@Autowired
	private ReplyMapper replyMapper;
	
	private S3Client s3;
	
	@Value("${aws.s3.bucketName}")
	private String bucketName;
	
	public List<BoardDto> listBoard(String type, String keyword) {
		// TODO Auto-generated method stub
		return mapper.selectBoardAll(type, "%" + keyword + "%");
	}
	
	//객체 생성되자마자 실행되는 메소드 (초기화니까 init 활용)
	@PostConstruct
	public void init() {
		Region region = Region.AP_NORTHEAST_2;
		this.s3 = S3Client.builder().region(region).build();
	}
	
	@PreDestroy
	public void destory() {
		this.s3.close();
	}

	@Transactional
	public boolean insertBoard(BoardDto board, MultipartFile[] files) {
//		board.setInserted(LocalDateTime.now());
		
		//게시글 등록
		int cnt = mapper.insertBoard(board);
		
		//파일 등록
		if(files != null) {
			for (MultipartFile file : files) {
				if (file.getSize()>0) {
					mapper.insertFile(board.getId(), file.getOriginalFilename());
					//		saveFile(board.getId(),file); // 파일 시스템에 저장(데스크탑)
					saveFileAwsS3(board.getId(), file); //aws s3 업로드
				}
			}
		}
		return cnt == 1;
	}

	private void saveFileAwsS3(int id, MultipartFile file) {
		String key = "board/" + id + "/" + file.getOriginalFilename();
		
		PutObjectRequest putObjectRequest = PutObjectRequest.builder()
				.acl(ObjectCannedACL.PUBLIC_READ) //권한이 뭔지
				.bucket(bucketName) //어느 버킷에 올릴건지
				.key(key) // =폴더명+파일명 // 버킷 만들었을 때, ex 그림 파일 넣을 때, (객체마다 구분하는) 키는 무엇인지
				.build();
		
		RequestBody requestBody;
		try {
			requestBody = RequestBody.fromInputStream(file.getInputStream(),file.getSize());
			s3.putObject(putObjectRequest, requestBody);
		
		} catch (IOException e) {
			e.printStackTrace();
			//이것이자바다 10장 (오류가 잡혔다고 끝이 아님, 계속 진행되도록)
			throw new RuntimeException(e);
		} 
		
		
		
		
	}

	private void saveFile(int id, MultipartFile file) {
		
		//디렉토리 만들기
		String pathStr ="C:/imgtmp/board/" + id +"/";
		File path = new File(pathStr);
		path.mkdirs();
		
		//작성할 파일
		File des = new File(pathStr + file.getOriginalFilename());
		
		try {
			// 파일 저장
			file.transferTo(des);
		} catch (IllegalStateException | IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public BoardDto getBoardById(int id) {

		BoardDto board = mapper.selectBoardById(id);
		List<String> fileNames = mapper.selectFileNameByBoard(id);
		
		board.setFileName(fileNames); //board에서도 여러 그림파일 볼수있게 하는
		
		return board;
	}

	public boolean updateBoard(BoardDto dto) {
		// TODO Auto-generated method stub
		return mapper.updateBoard(dto) == 1;
	}

	@Transactional
	public boolean deleteBoard(int id) {
		//파일 목록 읽기
		String fileName = mapper.selectFileByBoardId(id);
		
		//실제파일 삭제 - 데스크탑에 업로드했을 경우 파일 삭제
//		if(fileName != null && !fileName.isEmpty()) {
//			String folder = "C:/imgtmp/board/" + id + "/";
//			String path = folder + fileName;
//			
//			File file = new File(path);
//			file.delete();
//			
//			File dir = new File(folder);
//			dir.delete(); 
//			
//			
//		}
		
		//s3에서 지우는 코드(aws)
		
		deleteFromAwsS3(id, fileName);
		
		
		
		//파일테이블 삭제
		mapper.deleteFileByBoardId(id);
		
		//댓글 삭제
		replyMapper.deleteByBoardId(id);
		
		return mapper.deleteBoard(id) == 1;
	}

	private void deleteFromAwsS3(int id, String fileName) {
		String key = "board/" + id + "/" + fileName;
		
		DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
		.bucket(bucketName)
		.key(key)
		.build();
				
				
				
		s3.deleteObject(deleteObjectRequest);
		
	}


}




