package org.xdty.phone.number.util;

public final class Database {

    private Database() {
    }

    public static Database getInstance() {
        return SingletonHelper.INSTANCE;
    }

    private final static class SingletonHelper {
        private final static Database INSTANCE = new Database();
    }
}
