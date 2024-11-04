package ru.otus.hw.models.mongo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "comments")
public class CommentMongo {

    @Id
    private String id;

    private String content;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @DBRef(lazy = true)
    private BookMongo book;

}