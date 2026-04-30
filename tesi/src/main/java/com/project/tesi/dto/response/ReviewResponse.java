package com.project.tesi.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * DTO di risposta per una recensione.
 * Mostra solo il nome dell'autore (non il cognome) per motivi di privacy.
 */
@Data
public class ReviewResponse {

    private String authorName;

    private int rating;

    private String comment;

    private LocalDateTime date;

    private ReviewResponse() {}

    public static class Builder {
        private String authorName;
        private int rating;
        private String comment;
        private LocalDateTime date;

        public Builder authorName(String authorName) {
            this.authorName = authorName;
            return this;
        }

        public Builder rating(int rating) {
            this.rating = rating;
            return this;
        }

        public Builder comment(String comment) {
            this.comment = comment;
            return this;
        }

        public Builder date(LocalDateTime date) {
            this.date = date;
            return this;
        }

        public ReviewResponse build() {
            ReviewResponse obj = new ReviewResponse();
            obj.authorName = this.authorName;
            obj.rating = this.rating;
            obj.comment = this.comment;
            obj.date = this.date;
            return obj;
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}
