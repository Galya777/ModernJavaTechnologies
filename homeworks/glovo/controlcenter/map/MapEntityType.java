package bg.sofia.uni.fmi.mjt.glovo.controlcenter.map;

public enum MapEntityType {
    ROAD('.'),
    WALL('#'),
    RESTAURANT('R'),
    CLIENT('C'),
    DELIVERY_GUY_CAR('A'),
    DELIVERY_GUY_BIKE('B');

    private final char symbol;

    MapEntityType(char symbol) {
        this.symbol = symbol;
    }

    public char getSymbol() {
        return symbol;
    }

    public static MapEntityType fromSymbol(char symbol) {
        for (MapEntityType type : values()) {
            if (type.symbol == symbol) {
                return type;
            }
        }
        throw new IllegalArgumentException("No enum constant with symbol: " + symbol);
    }
}
