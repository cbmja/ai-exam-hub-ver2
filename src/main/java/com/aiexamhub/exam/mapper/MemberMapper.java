package com.aiexamhub.exam.mapper;

import com.aiexamhub.exam.dto.Member;

public interface MemberMapper {


    int save(Member form);

    int countByEmail(String email);

    int countByMemberId(String memberId);

    int countByPhone(String phone);


}
