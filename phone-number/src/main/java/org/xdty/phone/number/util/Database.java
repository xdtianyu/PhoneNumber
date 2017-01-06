package org.xdty.phone.number.util;

import com.rethinkdb.RethinkDB;
import com.rethinkdb.net.Connection;

public final class Database {

    public static final RethinkDB r = RethinkDB.r;

    private Database() {
        Connection conn = r.connection().hostname("backend.xdty.org").port(443).connect();
        r.db("test").tableCreate("tv_shows").run(conn);
        r.table("tv_shows").insert(r.hashMap("name", "Star Trek TNG")).run(conn);
    }

    public static Database getInstance() {
        return SingletonHelper.INSTANCE;
    }

    private final static class SingletonHelper {
        private final static Database INSTANCE = new Database();
    }
}
