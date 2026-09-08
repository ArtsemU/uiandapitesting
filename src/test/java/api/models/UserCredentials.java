package api.models;

import api.Config;

import java.util.Objects;

public final class UserCredentials {
    private final String userName;
    private final String password;

    private UserCredentials(Builder builder) {
        this.userName = builder.userName;
        this.password = builder.password;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String userName;
        private String password;

        public Builder userName(String userName) {
            this.userName = userName;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public UserCredentials build() {
            return new UserCredentials(this);
        }
    }

    private static String uniqueUserName(String base) {
        return base + System.currentTimeMillis();
    }

    public static UserCredentials unique() {
        return UserCredentials.builder()
                .userName(uniqueUserName("username_"))
                .password(Config.userPassword())
                .build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserCredentials that)) return false;
        return Objects.equals(userName, that.userName) && Objects.equals(password, that.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userName, password);
    }

    @Override
    public String toString() {
        return "UserCredentials{" +
                "userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
