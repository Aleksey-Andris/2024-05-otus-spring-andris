package ru.otus.hw.services;

import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.security.acls.jdbc.JdbcMutableAclService;
import org.springframework.security.acls.jdbc.LookupStrategy;
import org.springframework.security.acls.model.AclCache;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.Assert;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

public class JdbcMutableAclServiceImpl extends JdbcMutableAclService {

    public JdbcMutableAclServiceImpl(DataSource dataSource, LookupStrategy lookupStrategy, AclCache aclCache) {
        super(dataSource, lookupStrategy, aclCache);
    }

    @Override
    protected Long createOrRetrieveClassPrimaryKey(String type, boolean allowCreate, Class idType) {
        List<Long> classIds = this.jdbcOperations.queryForList("SELECT id FROM acl_class WHERE class=?",
                Long.class, type);
        if (!classIds.isEmpty()) {
            return classIds.get(0);
        }
        if (allowCreate) {
            return createClassPrimaryKey(type, idType);
        }
        return null;
    }

    private Long createClassPrimaryKey(String type, Class idType) {
        String sql;
        String[] params;
        if (!isAclClassIdSupported()) {
            sql = "INSERT INTO acl_class (class) VALUES (?)";
            params = new String[]{type};
        } else {
            sql = "INSERT INTO acl_class (class, class_id_type) VALUES (?, ?)";
            params = new String[]{type, idType.getCanonicalName()};
        }
        KeyHolder keyHolder = new GeneratedKeyHolder();
        this.jdbcOperations.update(connection -> {
                    PreparedStatement ps =
                            connection.prepareStatement(sql,
                                    Statement.RETURN_GENERATED_KEYS);
                    ps.setString(1, params[0]);
                    if (params.length > 1) {
                        ps.setString(2, params[1]);
                    }
                    return ps;
                }, keyHolder
        );
        Assert.isTrue(TransactionSynchronizationManager.isSynchronizationActive(), "Transaction must be running");
        return (Long) keyHolder.getKeys().get("id");
    }

    @Override
    protected Long createOrRetrieveSidPrimaryKey(String sidName, boolean sidIsPrincipal, boolean allowCreate) {
        List<Long> sidIds = this.jdbcOperations.queryForList("SELECT id FROM acl_sid WHERE principal=? AND sid=?",
                Long.class, sidIsPrincipal, sidName);
        if (!sidIds.isEmpty()) {
            return sidIds.get(0);
        }
        if (allowCreate) {
           return createSidPrimaryKey(sidName, sidIsPrincipal);
        }
        return null;
    }

    private Long createSidPrimaryKey(String sidName, boolean sidIsPrincipal) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        this.jdbcOperations.update(connection -> {
                    PreparedStatement ps =
                            connection.prepareStatement("INSERT INTO acl_sid (principal, sid) VALUES (?, ?)",
                                    Statement.RETURN_GENERATED_KEYS);
                    ps.setBoolean(1, sidIsPrincipal);
                    ps.setString(2, sidName);
                    return ps;
                }, keyHolder
        );
        Assert.isTrue(TransactionSynchronizationManager.isSynchronizationActive(), "Transaction must be running");
        return (Long) keyHolder.getKeys().get("id");
    }

}