package edu.goldenhammer.database.data_types;

import java.io.Serializable;

/**
 * Created by seanjib on 2/19/2017.
 */
public class DatabaseCity implements Serializable, IDatabaseCity{
    public static final String AMON_SUL = "Amon Sul";
    public static final String ASH_MOUNTAINS = "Ash Mountains";
    public static final String BARAD_DUR = "Barad Dur";
    public static final String BREE = "Bree";
    public static final String CROSSINGS_OF_POROS = "Crossings of Poros";
    public static final String DAGORLAD_BATTLE_PLAINS = "Dagorlad (Battle Plains)";
    public static final String DOL_GULDUR = "Dol Guldur";
    public static final String EAST_BIGHT = "East Bight";
    public static final String EDHELLOND = "Edhellond";
    public static final String EDORAS = "Edoras";
    public static final String EMYN_MUIL = "Emyn Muil";
    public static final String ERECH = "Erech";
    public static final String ERYN_VORN = "Eryn Vorn";
    public static final String ETTENMOORS = "Ettenmoors";
    public static final String FALLS_OF_RAUROS = "Falls of Rauros";
    public static final String FANGORN = "Fangorn";
    public static final String FORLINDON = "Forlindon";
    public static final String GREY_HAVENS = "Grey Havens";
    public static final String HARLINDON = "Harlindon";
    public static final String HELMS_DEEP = "Helm''s Deep";
    public static final String HOBBITON = "Hobbiton";
    public static final String IRON_HILLS = "Iron Hills";
    public static final String ISENGARD = "Isengard";
    public static final String LAKE_EVENDIM = "Lake Evendim";
    public static final String LOND_DAER = "Lond Daer";
    public static final String LORIEN = "Lorien";
    public static final String MINAS_MORGUL = "Minas Morgul";
    public static final String MINAS_TIRITH = "Minas Tirith";
    public static final String MORIAS_GATE = "Moria''s Gate";
    public static final String RAS_MORTHIL = "Ras Morthil";
    public static final String RIVENDELL = "Rivendell";
    public static final String SEA_OF_NURNEN = "Sea of Nurnen";
    public static final String SEA_OF_RHUN = "Sea of Rhun";
    public static final String THARBAD = "Tharbad";
    public static final String THE_LONELY_MOUNTAIN = "The Lonely Mountain";

    public static final int CITY_COUNT = 35;
    public static final String ID = "city_id";
    public static final String NAME = "city_name";
    public static final String POINT_X = "point_x";
    public static final String POINT_Y = "point_y";
    public static final String TABLE_NAME = "city";
    public static final String CREATE_STMT = String.format(
            "CREATE TABLE IF NOT EXISTS %1$s (" +
                    "%2$s SERIAL NOT NULL," +
                    "%3$s VARCHAR(30) UNIQUE," +
                    "%4$s INTEGER NOT NULL," +
                    "%5$s INTEGER NOT NULL," +
                    "PRIMARY KEY(%2$s)" +
                    ");",
            TABLE_NAME,
            ID,
            NAME,
            POINT_X,
            POINT_Y
            );
    public static final String INSERT_STMT = String.format(
            "INSERT INTO %1$s(%2$s, %3$s, %4$s) VALUES %5$s",
            TABLE_NAME,
            NAME,
            POINT_X,
            POINT_Y,
            getAllCities()
    );

    public DatabaseCity(String id, String name, int pointX, int pointY) {
        this.id = id;
        this.name = name;
        this.pointX = pointX;
        this.pointY = pointY;
    }

    @Override
    public String getID() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getPointX() {
        return pointX;
    }

    @Override
    public int getPointY() {
        return pointY;
    }

    public static String columnNames() {
        return String.join(",", ID, NAME);
    }

    public static String getAllCities() {
        String formattedCities = "";
        for(int i = 0; i < CITY_COUNT; i++) {
            formattedCities += getFormattedCity();
        }
        return formattedCities.substring(0, formattedCities.length() - 2) + ";"; //replaces the final comma with a semicolon
    }

    public static String getFormattedCity() {
        return "(?, ?, ?),\n";
    }

    private String id;
    private String name;
    private int pointX;
    private int pointY;
}
