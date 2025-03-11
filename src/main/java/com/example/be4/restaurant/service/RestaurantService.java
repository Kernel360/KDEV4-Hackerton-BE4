package com.example.be4.restaurant.service;

import com.example.be4.restaurant.domain.RestaurantCommentEntity;
import com.example.be4.restaurant.dto.*;
import com.example.be4.restaurant.exception.CommentNotFoundException;
import com.example.be4.restaurant.exception.InvalidPasswordException;
import com.example.be4.restaurant.exception.KeyWordNotInsertException;
import com.example.be4.restaurant.exception.PostNotFoundException;
import com.example.be4.restaurant.repository.RestaurantCommentRepository;
import com.example.be4.restaurant.repository.RestaurantInfoRepository;
import com.example.be4.restaurant.repository.RestaurantPostRepository;
import com.example.be4.restaurant.domain.RestaurantInfoEntity;
import com.example.be4.restaurant.domain.RestaurantPostEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class RestaurantService {

    private final CustomMapper mapper;
    private final RestaurantInfoRepository infoRepository;
    private final RestaurantPostRepository postRepository;
    private final RestaurantCommentRepository commentRepository;


    /* 식당 게시글 생성 (restaurant_info, restaurant_post 테이블 저장) */
    @Transactional
    public RestaurantResponseDto createRestaurant(RestaurantRequestDto reqDto) {
        // 주소를 기반으로 식당 정보 조회 (없으면 새로 생성, 저장)
        RestaurantInfoEntity infoEntity = getRestaurantInfoByAddress(reqDto.getAddress())
                .orElseGet(() -> {
                    RestaurantInfoEntity newInfo = mapper.requestToInfo(reqDto);
                    return infoRepository.save(newInfo);
                });

        // 요청 DTO를 Post 엔티티로 변환
        RestaurantPostEntity postEntity = mapper.requestToPost(reqDto);
        postEntity.setInfoEntity(infoEntity);

        // Post 엔티티 저장
        postRepository.save(postEntity);

        // 저장된 Post 엔티티를 Response DTO로 변환하여 반환
        return mapper.postToResponse(postEntity);
    }

    /* 식당 게시글 삭제 (Info 테이블은 삭제 안 함) */
    @Transactional
    public RestaurantResponseDto deletePost(Long id, RestaurantDeleteDto deleteDto) {
        // 게시글 존재 확인 및 가져오기
        RestaurantPostEntity postEntity = checkPostExist(id);

        // 비밀번호 검증
        checkPassword(postEntity.getPassword(), deleteDto.getPassword());
        // 게시글 삭제
        postRepository.delete(postEntity);

        // 삭제된 엔티티를 DTO로 변환하여 반환
        return mapper.postToResponse(postEntity);
    }

    /* 식당 게시글 수정 */
    @Transactional
    public RestaurantResponseDto updatePost(Long id, RestaurantUpdateDto updateDto) {
        // 게시글 존재 확인 및 가져오기
        RestaurantPostEntity postEntity = checkPostExist(id);

        // 비밀번호 검증
        checkPassword(postEntity.getPassword(), updateDto.getPassword());

        mapper.updateFromDto(updateDto, postEntity);  // 변경

        return mapper.postToResponse(postEntity);
    }

    /* 식당 게시글 상세 페이지 */
    @Transactional(readOnly = true)
    public RestaurantResponseDto getPost(Long postId) {

        // responseDto로 변환해서 반환
        return mapper.postToResponse(checkPostExist(postId));
    }

    /* 식당 게시글 목록 */
    @Transactional(readOnly = true)
    public List<RestaurantListResponseDto> getPostList() {
        List<RestaurantPostEntity> postEntities = postRepository.findAll();

        List<RestaurantListResponseDto> responseList = postEntities.stream()
                .map(postEntity -> mapper.postToListResponse(postEntity))
                .collect(Collectors.toList());
        return responseList;
    }

    /* 식당 게시글 검색 */
    @Transactional(readOnly = true)
    public List<RestaurantListResponseDto> getListByKeyword(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            throw new KeyWordNotInsertException("검색어를 입력해주세요.");
        }
        List<RestaurantPostEntity> postEntities = postRepository.searchByTitle(keyword);

        List<RestaurantListResponseDto> responseList = postEntities.stream()
                .map(postEntity -> mapper.postToListResponse(postEntity))
                .collect(Collectors.toList());

        return responseList;
    }



    /* DB의 restaurant_info 테이블에 해당 식당 주소가 있는지 확인하는 메소드 */
    private Optional<RestaurantInfoEntity> getRestaurantInfoByAddress(String address) {
        return infoRepository.findRestaurantInfoEntityByAddress(address);
    }

    /* 게시글 있는지 검증(있으면 가져옴) */
    private RestaurantPostEntity checkPostExist(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new PostNotFoundException("게시글을 찾을 수 없습니다. ID: " + id));
    }

    /* 비밀번호 검증 */
    private Boolean checkPassword(String entityPassword, String dtoPassword) {
        // 비밀번호 검증
        if (!entityPassword.equals(dtoPassword)) {
            throw new InvalidPasswordException("비밀번호가 일치하지 않습니다.");
        }
        else{
            return true;
        }
    }

    /* 모든 댓글 조회 */
    @Transactional(readOnly = true)
    public List<RestaurantCommentResponseDto> getComments(Long postId) {
        RestaurantPostEntity postEntity = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("게시글을 찾을 수 없습니다. ID: " + postId));
        List<RestaurantCommentEntity> result = commentRepository.findAllByPostEntity(postEntity);
        return RestaurantCommentResponseDto.of(result);
    }

    /* 댓글 수정 */
    @Transactional
    public RestaurantCommentResponseDto updateComment(Long postId, Long commentId, RestaurantCommentRequestDto restaurantCommentDto) {
        RestaurantPostEntity postEntity = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("게시글을 찾을 수 없습니다. ID: " + postId));
        RestaurantCommentEntity comment = commentRepository.findByIdAndPostEntity(commentId, postEntity);
        if (comment == null) {
            throw new CommentNotFoundException("댓글을 찾을 수 없습니다. ID: " + commentId);
        }

        // 비밀번호 검증
        checkPassword(comment.getPassword(), restaurantCommentDto.getPassword());

        comment.setContent(restaurantCommentDto.getContent());
        comment.setPassword(restaurantCommentDto.getPassword());
        return RestaurantCommentResponseDto.of(comment);
    }

    /* 댓글 생성 */
    @Transactional
    public RestaurantCommentResponseDto createComment(Long postId, RestaurantCommentRequestDto commentRequestDto) {
        RestaurantPostEntity postEntity = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("게시글을 찾을 수 없습니다. ID: " + postId));
        RestaurantCommentEntity commentEntity = commentRepository.save(RestaurantCommentEntity.of(postEntity, commentRequestDto));
        return RestaurantCommentResponseDto.of(commentEntity);
    }

    /* 댓글 삭제 */
    @Transactional
    public void deleteComment(Long commentId, RestaurantDeleteDto deleteDto) {
        RestaurantCommentEntity comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("댓글을 찾을 수 없습니다. ID: " + commentId));

        // 비밀번호 검증
        checkPassword(comment.getPassword(), deleteDto.getPassword());

        commentRepository.delete(comment);
    }
}