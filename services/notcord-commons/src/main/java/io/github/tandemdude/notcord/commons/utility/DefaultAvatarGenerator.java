package io.github.tandemdude.notcord.commons.utility;

import java.util.List;

public class DefaultAvatarGenerator {
    private static final List<List<String>> colourPalettes = List.of(
        List.of("#E6B39A", "#E6CBA5", "#EDE3B4", "#8B9E9B", "#6D7578"),
        List.of("#EC4B59", "#9A2848", "#130716", "#FC8C77", "#F8DFBD"),
        List.of("#E8D7A9", "#8EAA94", "#6B666D", "#6C3751", "#52223C"),
        List.of("#21203F", "#FFF1CE", "#E7BFA5", "#C5A898", "#4B3C5D")
        //            "420B58,FC036C,F1A20B,8D9C09,08807B"  // this palette is very exotic - maybe too bright
    );

    private static List<String> getPalette(int number) {
        return colourPalettes.get(number % colourPalettes.size());
    }

    private static String getColour(int n, List<String> colours) {
        return colours.get(n % colours.size());
    }

    private static int hashCodeOf(String value) {
        var code = 0;
        for (char c : value.toCharArray()) {
            code = ((code << 5) - code) + c;
            code &= code;
        }
        return Math.abs(code);
    }

    private static int getDigit(int number, int ntn) {
        return (int) Math.floor((number / Math.pow(10, ntn)) % 10);
    }

    private static boolean getBoolean(int number, int ntn) {
        return (getDigit(number, ntn) % 2) == 0;
    }

    private static int getUnit(int number, int range, Integer index) {
        var value = number % range;

        return (index != null && index != 0) && (getDigit(number, index) % 2) == 0 ? -value : value;
    }

    private static String getContrast(String colour) {
        colour = colour.replace("#", "");

        var r = Integer.parseInt(colour.substring(0, 2), 16);
        var g = Integer.parseInt(colour.substring(2, 4), 16);
        var b = Integer.parseInt(colour.substring(4, 6), 16);

        var yiq = ((r * 299L) + (g * 587L) + (b * 114L)) / 1000;
        return yiq >= 128 ? "#000000" : "#FFFFFF";
    }

    public static String generateDefaultAvatarSvg(String username) {
        var nameHashCode = hashCodeOf(username);
        var palette = getPalette(nameHashCode);

        var preTranslateX = getUnit(nameHashCode, 10, 1);
        var preTranslateY = getUnit(nameHashCode, 10, 2);

        var backgroundColour = getColour(nameHashCode + 13, palette);

        var wrapperColour = getColour(nameHashCode, palette);
        var wrapperTranslateX = preTranslateX < 5 ? preTranslateX + 36 / 9 : preTranslateX;
        var wrapperTranslateY = preTranslateY < 5 ? preTranslateY + 36 / 9 : preTranslateY;
        var wrapperRotate = getUnit(nameHashCode, 360, null);
        var wrapperScale = 1 + getUnit(nameHashCode, 3, null) / 10F;

        var mouthOpen = getBoolean(nameHashCode, 2);
        var mouthSpread = getUnit(nameHashCode, 3, null);

        var eyeSpread = getUnit(nameHashCode, 5, null);

        var faceColour = getContrast(wrapperColour);
        var faceTranslateX = wrapperTranslateX > 6 ? wrapperTranslateX / 2 : getUnit(nameHashCode, 8, 1);
        var faceTranslateY = wrapperTranslateY > 6 ? wrapperTranslateY / 2 : getUnit(nameHashCode, 7, 2);
        var faceRotate = getUnit(nameHashCode, 10, 3);

        var isCircle = getBoolean(nameHashCode, 1);

        return String.format(
            """
                <svg viewBox="0 0 36 36" fill="none" role="img" xmlns="http://www.w3.org/2000/svg" width="80" height="80">
                    <title>%s</title>
                    <mask id="mask__beam" maskUnits="userSpaceOnUse" x="0" y="0" width="36" height="36">
                        <rect width="36" height="36" rx="72" fill="#FFFFFF"></rect>
                    </mask>
                    <g mask="url(#mask__beam)">
                        <rect width="36" height="36" fill="%s"></rect>
                        <rect x="0" y="0" width="36" height="36" transform="translate(%s %s) rotate(%s 18 18) scale(%.1f)" fill="%s" rx="%s"></rect>
                        <g transform="translate(%s %s) rotate(%s 18 18)">
                            %s
                            <rect x="%s" y="14" width="1.5" height="2" rx="1" stroke="none" fill="%s"></rect>
                            <rect x="%s" y="14" width="1.5" height="2" rx="1" stroke="none" fill="%s"></rect>
                        </g>
                    </g>
                </svg>
                """,
            username,
            backgroundColour,
            wrapperTranslateX, wrapperTranslateY, wrapperRotate, wrapperScale, wrapperColour,
            isCircle ? 36 : 6,
            faceTranslateX, faceTranslateY, faceRotate,
            mouthOpen ? String.format(
                """
                    <path d="M15 %sc2 1 4 1 6 0" stroke="%s" fill="none" stroke-linecap="round"></path>
                    """, 19 + mouthSpread, faceColour) : String.format(
                """
                    <path d="M13 %sa1,0.75 0 0,0 10,0" fill="%s"></path>
                    """, 19 + mouthSpread, faceColour),
            14 - eyeSpread, faceColour, 20 + eyeSpread, faceColour
        ).replace("\n", "").replace("    ", "");
    }

    public static String generateDefaultAppIconSvg(String appName) {
        var nameHashCode = hashCodeOf(appName);
        var palette = getPalette(nameHashCode);

        var e0_color = getColour(nameHashCode, palette);

        var e1_color = getColour(nameHashCode + 1, palette);
        var e1_translateX = getUnit(nameHashCode * 2, 1, 1);
        var e1_translateY = getUnit(nameHashCode * 2, 1, 2);
        var e1_rotate = getUnit(nameHashCode * 2, 360, null);
        var e1_isSquare = getBoolean(nameHashCode, 2);

        var e2_color = getColour(nameHashCode + 2, palette);
        var e2_translateX = getUnit(nameHashCode * 3, -1, 1);
        var e2_translateY = getUnit(nameHashCode * 3, -1, 2);

        var e3_color = getColour(nameHashCode + 3, palette);
        var e3_translateX = getUnit(nameHashCode * 4, -2, 1);
        var e3_translateY = getUnit(nameHashCode * 4, -2, 2);
        var e3_rotate = getUnit(nameHashCode * 4, 360, null);

        return String.format(
            """
                <svg viewBox="0 0 80 80" fill="none" role="img" xmlns="http://www.w3.org/2000/svg" width="80" height="80">
                    <title>%s</title>
                    <mask id="mask__bauhaus" maskUnits="userSpaceOnUse" x="0" y="0" width="80" height="80">
                        <rect width="80" height="80" rx="160" fill="#FFFFFF"></rect>
                    </mask>
                    <g mask="url(#mask__bauhaus)">
                        <rect width="80" height="80" fill="%s"></rect>
                        <rect x="10" y="30" width="80" height="%s" fill="%s" transform="translate(%s %s) rotate(%s 40 40)"></rect>
                        <circle cx="40" cy="40" fill="%s" r="16" transform="translate(%s %s)"></circle>
                        <line x1="0" y1="40" x2="80" y2="40" stroke-width="2" stroke="%s" transform="translate(%s %s) rotate(%s 40 40)"></line>
                    </g>
                </svg>
                """, appName, e0_color, e1_isSquare ? 80 : 10, e1_color, e1_translateX, e1_translateY, e1_rotate,
            e2_color, e2_translateX, e2_translateY, e3_color, e3_translateX, e3_translateY, e3_rotate
        ).replace("\n", "").replace("    ", "");
    }
}
