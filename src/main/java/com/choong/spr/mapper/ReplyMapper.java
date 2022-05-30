package com.choong.spr.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.choong.spr.domain.ReplyDto;

public interface ReplyMapper {

	int insertReply(ReplyDto dto);

	List<ReplyDto> selectAllBoardId(@Param("boardId")int boardId, @Param("memberId")String memberId);

	int updateReply(ReplyDto dto);

	void deleteByBoardId(int boardId);

	ReplyDto selectReplyById(int id);

	int deleteReply(int id);

	void deleteByMemberId(String memberId); //개수 몇개일지 모름 0개 지워질수도 있으니 int말고

}
