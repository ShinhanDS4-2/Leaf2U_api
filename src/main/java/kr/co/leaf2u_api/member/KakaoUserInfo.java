package kr.co.leaf2u_api.member;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class KakaoUserInfo {

    @JsonProperty("id")
    private Long kakaoId;

    @JsonProperty("kakao_account")
    private KakaoAccount kakaoAccount;

    @JsonProperty("properties")
    private Properties properties = new Properties(); // 기본 초기화

    public String getEmail() {
        return kakaoAccount != null && kakaoAccount.email != null ? kakaoAccount.email : null;
    }

    public String getNickname() {
        return properties != null && properties.nickname != null ? properties.nickname : null;
    }
    public String getGender() {
        return kakaoAccount != null && kakaoAccount.gender != null ? kakaoAccount.gender : "unknown";
    }

    public String getBirthyear() {
        return kakaoAccount != null && kakaoAccount.birthyear != null ? kakaoAccount.birthyear : "0000-00-00";
    }

    public String getBirthday() {
        return kakaoAccount != null && kakaoAccount.birthday != null ? kakaoAccount.birthday : "0000-00-00";
    }

    public String getPhone_number() {
        return kakaoAccount != null && kakaoAccount.phone_number != null ? kakaoAccount.phone_number : "010-0000-0000";
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class KakaoAccount {

        @JsonProperty("email")
        private String email;

        @JsonProperty("gender")
        private String gender;

        @JsonProperty("birthyear")
        private String birthyear;

        @JsonProperty("birthday")
        private String birthday;

        @JsonSetter("birthday")
        public void setBirthday(String birthday) {
            if (birthday != null && birthday.length() == 4) {
                this.birthday = birthday.substring(0, 2) + "-" + birthday.substring(2, 4);
            } else {
                this.birthday = birthday;
            }
        }

        @JsonProperty("phone_number")
        private String phone_number;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Properties {
        @JsonProperty("nickname")
        private String nickname;
    }
}