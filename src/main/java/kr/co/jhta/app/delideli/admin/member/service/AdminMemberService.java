package kr.co.jhta.app.delideli.admin.member.service;

import kr.co.jhta.app.delideli.admin.member.domain.AdminMemberAdmin;
import kr.co.jhta.app.delideli.client.account.domain.ClientAccount;

import java.util.List;

public interface AdminMemberService {

    //사장님 회원 목록
    List<AdminMemberAdmin> getAllclientList();

    // 승인 상태를 토글하는 메서드
    public int toggleClientAccess(int clientKey);

}
