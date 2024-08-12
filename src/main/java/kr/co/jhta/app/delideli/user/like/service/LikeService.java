package kr.co.jhta.app.delideli.user.like.service;

import kr.co.jhta.app.delideli.user.like.domain.Like;

public interface LikeService {

    Like findLikeByUserAndStore(int userKey, int storeInfoKey);

    void toggleLike(int userKey, int storeInfoKey);
}
