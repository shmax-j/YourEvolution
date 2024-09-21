package shmax.entities.bacteria;

public enum BacteriaModifications {
    Flagellum(BMod.Flagellum), Nucleus(BMod.Nucleus);

    public BMod mod;

    BacteriaModifications(BMod mod) {
        this.mod = mod;
    }
}
