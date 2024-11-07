package ru.otus.hw.repositories.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.otus.hw.models.mongo.CommentMongo;

public interface CommentRepositoryMongo extends MongoRepository<CommentMongo, String> {

}
