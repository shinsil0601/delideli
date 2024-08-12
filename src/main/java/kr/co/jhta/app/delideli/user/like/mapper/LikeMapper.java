package kr.co.jhta.app.delideli.user.like.mapper;

import kr.co.jhta.app.delideli.user.like.domain.Like;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LikeMapper {
    Like findLikeByUserAndStore(int userKey, int storeInfoKey);

    void insertLike(Like like);

    void deleteLike(int likeKey);
}
