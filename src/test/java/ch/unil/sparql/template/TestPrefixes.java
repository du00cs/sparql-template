package ch.unil.sparql.template;

import org.apache.commons.collections4.MapUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Prefixes and their namespaces used in tests.
 *
 * @author gushakov
 * @see #TEST_PREFIXES
 */
public class TestPrefixes {

    // BNB LOD data

    public static final String BNP = "bnp";
    public static final String BNP_NS = "http://bnb.data.bl.uk/id/person/";

    public static final String BLT = "blt";
    public static final String BLT_NS = "http://www.bl.uk/schemas/bibliographic/blterms#";

    // Purl DC

    public static final String PLT =  "plt";
    public static final String PLT_NS =  "http://purl.org/dc/terms/";

    // IFLA

    public static final String IFE = "ife";
    public static final String IFE_NS = "http://iflastandards.info/ns/isbd/elements/";

    // example

    public static final String EXR = "exr";
    public static final String EXR_NS = "http://example.org/resource#";

    public static final String EXP = "exp";
    public static final String EXP_NS = "http://example.org/property#";

    public static final Map<String, String> TEST_PREFIXES = MapUtils.putAll(new HashMap<>(),
            new String[]{
                    BNP, BNP_NS,
                    BLT, BLT_NS,
                    PLT, PLT_NS,
                    IFE, IFE_NS,
                    EXR, EXR_NS,
                    EXP, EXP_NS
            });

}
