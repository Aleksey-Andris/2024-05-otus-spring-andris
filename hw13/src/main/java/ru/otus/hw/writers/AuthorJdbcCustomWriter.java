package ru.otus.hw.writers;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import ru.otus.hw.dto.AuthorDTO;
import ru.otus.hw.writers.dto.IdLink;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class AuthorJdbcCustomWriter implements ItemWriter<AuthorDTO> {

    private final NamedParameterJdbcOperations namedParameterJdbcOperations;

    public AuthorJdbcCustomWriter(NamedParameterJdbcOperations namedParameterJdbcOperations) {
        this.namedParameterJdbcOperations = namedParameterJdbcOperations;
    }

    @Override
    public void write(Chunk<? extends AuthorDTO> chunk) {
        if (chunk.isEmpty()) {
            return;
        }
        List<IdLink> idLinks = saveAuthors(chunk.getItems());
        saveIdLinks(idLinks);
    }

    private List<IdLink> saveAuthors(List<? extends AuthorDTO> items) {
        var keyHolder = new GeneratedKeyHolder();
        namedParameterJdbcOperations.batchUpdate("INSERT INTO authors (full_name) VALUES (:fullName)",
                SqlParameterSourceUtils.createBatch(items), keyHolder, new String[]{"id"});
        List<IdLink> idLinks = new LinkedList<>();
        int index = 0;
        for (Map<String, Object> map : keyHolder.getKeyList()) {
            idLinks.add(new IdLink((long) map.get("id"), items.get(index).getId()));
            index++;
        }
        return idLinks;
    }

    private void saveIdLinks(List<IdLink> idLinks) {
        namedParameterJdbcOperations.batchUpdate(
                "INSERT INTO author_ids_temp (id_inner, id_external) VALUES (:idInner, :idExternal)",
                SqlParameterSourceUtils.createBatch(idLinks));
    }

}
