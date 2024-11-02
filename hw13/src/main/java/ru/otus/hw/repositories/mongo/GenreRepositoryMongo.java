package ru.otus.hw.repositories.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.otus.hw.models.mongo.GenreMongo;

import java.util.List;
import java.util.Set;

public interface GenreRepositoryMongo extends MongoRepository<GenreMongo, String> {

    List<GenreMongo> findByIdIn(Set<String> ids);

}
