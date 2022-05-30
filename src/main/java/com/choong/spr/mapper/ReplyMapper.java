package com.choong.spr.mapper;

import java.util.List;

import com.choong.spr.domain.ReplyDto;

public interface ReplyMapper {

	int insertReply(ReplyDto dto);

	List<ReplyDto> selectAllBoardId(int boardId);

	int updateReply(ReplyDto dto);


	void deleteByBoardId(int boardId);

	ReplyDto selectReplyById(int id);

	int deleteReply(int id);

}
