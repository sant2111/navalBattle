package com.example.navalbattle.view;

import javafx.scene.paint.Color;

/**
 * Paleta de colores compartida por todas las figuras 2D del juego:
 * cascos y detalles de barcos ({@code view.shapes}) y marcadores de
 * estado (agua, tocado, hundido).
 * <p>
 * Estas figuras se dibujan con {@code javafx.scene.shape} directamente
 * en Java (no vía CSS): esta clase es su única fuente de colores para
 * que ningún barco o marcador termine con un hex suelto y distinto al
 * resto.
 * <p>
 * Heurística 4 de Nielsen (consistencia y estándares): todo barco y todo
 * marcador de un mismo tipo se ve exactamente igual en cualquier vista
 * del juego (panel de flota, grilla de colocación, tablero de combate).
 *
 * @author Jorge Navia
 * @version 1.0
 */
public final class GameColors {

    /** Relleno del casco de cualquier barco. */
    public static final Color HULL_FILL = Color.web("#4A5D6E");

    /** Borde del casco de cualquier barco. */
    public static final Color HULL_BORDER = Color.web("#2C3E50");

    /** Cubierta y detalles claros sobre el casco (pista de vuelo, etc.). */
    public static final Color DECK_DETAIL = Color.web("#8B9DAF");

    /** Torretas, cañones y torres de mando: el detalle más oscuro. */
    public static final Color TURRET_DARK = Color.web("#1C2833");

    /** Marcas/franjas claras sobre la cubierta (líneas de pista, etc.). */
    public static final Color RUNWAY_STRIPE = Color.web("#F4F8FB");

    /**
     * Color de la marca de "agua" (disparo fallido). Deliberadamente gris
     * neutro, distinto del rojo de "tocado", para que ambas marcas no se
     * confundan de un vistazo (heurística 4 de Nielsen).
     */
    public static final Color WATER_MARK = Color.web("#5C6B73");

    /** Color exterior de la marca de "tocado". */
    public static final Color HIT_MARK_OUTER = Color.web("#E74C3C");

    /** Color interior de la marca de "tocado". */
    public static final Color HIT_MARK_INNER = Color.web("#F1C40F");

    /** Tono rojo de las llamas en la marca de "hundido". */
    public static final Color SUNK_FLAME_RED = Color.web("#C0392B");

    /** Tono naranja de las llamas en la marca de "hundido". */
    public static final Color SUNK_FLAME_ORANGE = Color.web("#E67E22");

    /** Tono gris del humo en la marca de "hundido". */
    public static final Color SUNK_SMOKE_GRAY = Color.web("#7F8C8D");

    private GameColors() {
    }
}
