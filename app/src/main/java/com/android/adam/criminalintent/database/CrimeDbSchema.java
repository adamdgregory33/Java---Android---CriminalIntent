package com.android.adam.criminalintent.database;

/**
 * Created by Adam on 03/08/2017.
 */

public class CrimeDbSchema {
    public static final class CrimeTable{

        public static final String NAME = "crimes";


            public static final class Cols{

                public static final String UUID = "uuid";
                public static final String TITLE = "title";
                public static final String DATE = "date";
                public static final String SOLVED = "solved";
                public static final String TIME = "time";
                public static final String SUSPECT = "suspect";
            }
        }
    }




