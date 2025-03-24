package kr.co.leaf2u_api.config;

import lombok.Getter;
import lombok.Setter;

public class TokenContext {

    private static final ThreadLocal<Long> memberIdxThreadLocal = new ThreadLocal<>();
    private static final ThreadLocal<Long> savingAccountIdxThreadLocal = new ThreadLocal<>();

    public static void setMemberIdx(Long memberIdx) {
        memberIdxThreadLocal.set(memberIdx);
    }

    public static Long getMemberIdx() {
        return memberIdxThreadLocal.get();
    }

    public static void setSavingAccountIdx(Long savingAccountIdx) {
        savingAccountIdxThreadLocal.set(savingAccountIdx);
    }

    public static Long getSavingAccountIdx() {
        return savingAccountIdxThreadLocal.get();
    }

    public static void clear() {
        memberIdxThreadLocal.remove();
        savingAccountIdxThreadLocal.remove();
    }
}
