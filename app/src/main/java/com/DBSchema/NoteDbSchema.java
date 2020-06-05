package com.DBSchema;

public class NoteDbSchema {
    public static final class NoteTable {
        public static final String NAME="notes";

        public static final class Cols{
            public static final String UUID="uuid";
            public static final String TITLE="title";
            public static final String DATE="date";
            public static final String COLLECTED="liked";
            public static final String DETAIL="detail";
            public static final String PICTURE="picturePath";
            public static final String LINK="link";
        }
    }
}
