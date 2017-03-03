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

    public static final String ID = "city_id";
    public static final String NAME = "city_name";
    public static final String POINT_X = "point_x";
    public static final String POINT_Y = "point_y";
    public static final String TABLE_NAME = "city";
    public static final String CREATE_STMT = String.format(
            "CREATE TABLE IF NOT EXISTS %1$s (" +
                    "%2$s SERIAL NOT NULL," +
                    "%3$s VARCHAR(30) UNIQUE," +
                    "%4$s DECIMAL NOT NULL," +
                    "%5$s DECIMAL NOT NULL," +
                    "PRIMARY KEY(%2$s)" +
                    ");" +
                    "INSERT INTO %1$s(%3$s, %4$s, %5$s) VALUES %6$s",
            TABLE_NAME,
            ID,
            NAME,
            POINT_X,
            POINT_Y,
            getAllCities()
            );

    public DatabaseCity(String id, String name, double pointX, double pointY) {
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
    public double getPointX() {
        return pointX;
    }

    @Override
    public double getPointY() {
        return pointY;
    }

    public static String columnNames() {
        return String.join(",", ID, NAME);
    }

    public static String getAllCities() {
        String formattedCities =
                getFormattedCity(AMON_SUL, 0, 0) +
                getFormattedCity(ASH_MOUNTAINS, 0, 0) +
                getFormattedCity(BARAD_DUR, 0, 0) +
                getFormattedCity(BREE, 0, 0) +
                getFormattedCity(CROSSINGS_OF_POROS, 0, 0) +
                getFormattedCity(DAGORLAD_BATTLE_PLAINS, 0, 0) +
                getFormattedCity(DOL_GULDUR, 0, 0) +
                getFormattedCity(EAST_BIGHT, 0, 0) +
                getFormattedCity(EDHELLOND, 0, 0) +
                getFormattedCity(EDORAS, 0, 0) +
                getFormattedCity(EMYN_MUIL, 0, 0) +
                getFormattedCity(ERECH, 0, 0) +
                getFormattedCity(ERYN_VORN, 0, 0) +
                getFormattedCity(ETTENMOORS, 0, 0) +
                getFormattedCity(FALLS_OF_RAUROS, 0, 0) +
                getFormattedCity(FANGORN, 0, 0) +
                getFormattedCity(FORLINDON, 0, 0) +
                getFormattedCity(GREY_HAVENS, 0, 0) +
                getFormattedCity(HARLINDON, 0, 0) +
                getFormattedCity(HELMS_DEEP, 0, 0) +
                getFormattedCity(HOBBITON, 0, 0) +
                getFormattedCity(IRON_HILLS, 0, 0) +
                getFormattedCity(ISENGARD, 0, 0) +
                getFormattedCity(LAKE_EVENDIM, 0, 0) +
                getFormattedCity(LOND_DAER, 0, 0) +
                getFormattedCity(LORIEN, 0, 0) +
                getFormattedCity(MINAS_MORGUL, 0, 0) +
                getFormattedCity(MINAS_TIRITH, 0, 0) +
                getFormattedCity(MORIAS_GATE, 0, 0) +
                getFormattedCity(RAS_MORTHIL, 0, 0) +
                getFormattedCity(RIVENDELL, 0, 0) +
                getFormattedCity(SEA_OF_NURNEN, 0, 0) +
                getFormattedCity(SEA_OF_RHUN, 0, 0) +
                getFormattedCity(THARBAD, 0, 0) +
                getFormattedCity(THE_LONELY_MOUNTAIN, 0, 0);
        return formattedCities.substring(0, formattedCities.length() - 1) + ';'; //replaces the final comma with a semicolon
    }

    public static String getFormattedCity(String cityName, double pointX, double pointY) {
        return String.format("('%1$s', %2$f, %3$f),",
                cityName,
                pointX,
                pointY);
    }

    private String id;
    private String name;
    private double pointX;
    private double pointY;
}
