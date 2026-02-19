package aleosh.online.mediaserver.features.users.domain.entities;

import java.util.UUID;

public class User {

    private final UUID id;
    private final String username;
    private final String email;
    private final String password;
    private final String photo;

    public User(Builder builder) {
        this.id = builder.id;
        this.username = builder.username;
        this.email = builder.email;
        this.password = builder.password;
        this.photo = builder.photo;
    }

    public static Builder builder() { return new Builder(); }

    public UUID getId() { return id; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getPhoto() { return photo; }

    public static class Builder {
        private UUID id;
        private String username;
        private String email;
        private String password;
        private String photo;

        public Builder id(UUID id) {
            this.id = id;
            return this;
        }

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public Builder photo(String photo) {
            this.photo = photo;
            return this;
        }

        public User build() { return new User(this); }
    }

}
