package com.example.navalbattle.view.shapes;

import com.example.navalbattle.model.Orientation;
import com.example.navalbattle.model.ShipType;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.HashSet;
import java.util.Set;

/**
 * Clase base de toda figura 2D de barco: fija la caja lógica de la
 * composición (largo según el tipo, ancho fijo) según la orientación
 * actual, aplica una sombra sutil para dar volumen, y ofrece la API
 * común de orientación y de marcado de impactos que necesitan tanto la
 * colocación (drag-and-drop) como el futuro guardado/restauración de
 * partida.
 * <p>
 * Cada subclase (una por tipo de barco) solo implementa
 * {@link #buildShape()}: construye su silueta con primitivas de
 * {@code javafx.scene.shape} dentro del rectángulo lógico de
 * {@link #length()} x {@link #thickness()}, usando {@link #x} / {@link #y}
 * para mapear cada punto según la orientación actual.
 * <p>
 * La orientación se resuelve remapeando ejes ("a lo largo"/"a lo ancho"
 * → X/Y) en vez de rotar el {@code Node} con {@code Rotate}: una
 * rotación de nodo no actualiza el tamaño que reservan los contenedores
 * JavaFX (como {@code HBox}) para el barco, y el panel de flota quedaría
 * mal alineado.
 * <p>
 * Heurística 2 de Nielsen (coincidencia con el mundo real): cada
 * subclase dibuja una silueta naval reconocible. Heurística 4
 * (consistencia): todas comparten {@link GameColors} y esta misma base.
 *
 * @author Jorge Navia
 * @version 1.0
 */
public abstract class ShipView extends Group {

    /** Fracción de {@code cellSize} usada como ancho (grosor) del casco. */
    protected static final double HULL_WIDTH_FACTOR = 0.75;

    /** Margen interno (10%) para no tocar los bordes de la celda. */
    protected static final double INNER_MARGIN_FACTOR = 0.10;

    /** Tipo de barco que representa esta figura. */
    protected final ShipType type;

    /** Tamaño en píxeles de una casilla del tablero. */
    protected final double cellSize;

    /** Orientación actual con la que se dibuja la silueta. */
    protected Orientation orientation;

    private final Set<Integer> hitCellIndexes = new HashSet<>();
    private boolean sunk;

    /**
     * @param type        tipo de barco que esta figura representa
     * @param cellSize    tamaño en píxeles de una casilla del tablero
     * @param orientation orientación inicial con la que se dibuja
     */
    protected ShipView(ShipType type, double cellSize, Orientation orientation) {
        this.type = type;
        this.cellSize = cellSize;
        this.orientation = orientation;
        setEffect(new DropShadow(4, Color.rgb(0, 0, 0, 0.35)));
        buildShape();
    }

    /** @return longitud total de la silueta a lo largo del eje del barco */
    protected double length() {
        return type.getSizeInCells() * cellSize - cellSize * INNER_MARGIN_FACTOR * 2;
    }

    /** @return ancho total de la silueta, perpendicular al eje del barco */
    protected double thickness() {
        return cellSize * HULL_WIDTH_FACTOR;
    }

    /** @return {@code true} si el barco se dibuja horizontalmente */
    protected boolean isHorizontal() {
        return orientation == Orientation.HORIZONTAL;
    }

    /**
     * Mapea una coordenada lógica (a lo largo del barco, a lo ancho) a X,
     * según la orientación actual.
     *
     * @param along  distancia a lo largo del eje del barco
     * @param across distancia a lo ancho del eje del barco
     * @return coordenada X resultante
     */
    protected double x(double along, double across) {
        return isHorizontal() ? along : across;
    }

    /**
     * Mapea una coordenada lógica (a lo largo del barco, a lo ancho) a Y,
     * según la orientación actual.
     *
     * @param along  distancia a lo largo del eje del barco
     * @param across distancia a lo ancho del eje del barco
     * @return coordenada Y resultante
     */
    protected double y(double along, double across) {
        return isHorizontal() ? across : along;
    }

    /** @return ancho en X del casco completo, según la orientación actual */
    protected double hullWidth() {
        return isHorizontal() ? length() : thickness();
    }

    /** @return alto en Y del casco completo, según la orientación actual */
    protected double hullHeight() {
        return isHorizontal() ? thickness() : length();
    }

    /**
     * Construye un rectángulo de detalle (cubierta, torre, etc.) ubicado
     * en coordenadas lógicas "a lo largo/a lo ancho" del barco, ya
     * mapeado a X/Y según la orientación actual.
     *
     * @param along    posición a lo largo del eje del barco
     * @param across   posición a lo ancho del eje del barco
     * @param alongSize  tamaño a lo largo del eje del barco
     * @param acrossSize tamaño a lo ancho del eje del barco
     * @param fill     color de relleno del rectángulo
     * @return el rectángulo ya posicionado y coloreado
     */
    protected Rectangle buildDetailRectangle(double along, double across, double alongSize, double acrossSize, Color fill) {
        Rectangle rectangle = new Rectangle(x(along, across), y(along, across),
                isHorizontal() ? alongSize : acrossSize,
                isHorizontal() ? acrossSize : alongSize);
        rectangle.setFill(fill);
        return rectangle;
    }

    /**
     * Construye la composición de {@code Shape} propia de este tipo de
     * barco. Se invoca al crear la figura y cada vez que cambia su
     * estado visual (orientación, impacto, hundimiento).
     */
    protected abstract void buildShape();

    /**
     * Desplazamiento en X que hay que sumarle a la posición de la celda
     * inicial en una grilla para que esta figura quede centrada dentro
     * de su huella (la silueta se dibuja más chica que la celda, con
     * margen interno).
     *
     * @return desplazamiento en X, en píxeles
     */
    public double getGridOffsetX() {
        return isHorizontal() ? cellSize * INNER_MARGIN_FACTOR : (cellSize - thickness()) / 2;
    }

    /**
     * Desplazamiento en Y análogo a {@link #getGridOffsetX()}.
     *
     * @return desplazamiento en Y, en píxeles
     */
    public double getGridOffsetY() {
        return isHorizontal() ? (cellSize - thickness()) / 2 : cellSize * INNER_MARGIN_FACTOR;
    }

    /**
     * Cambia la orientación y reconstruye la silueta completa (incluidos
     * los impactos ya marcados).
     *
     * @param newOrientation nueva orientación
     */
    public void setOrientation(Orientation newOrientation) {
        this.orientation = newOrientation;
        redraw();
    }

    /**
     * Marca como impactada una casilla específica de este barco. Se usa,
     * por ejemplo, al restaurar una partida guardada: el controlador
     * invoca este método por cada casilla que ya estaba tocada.
     *
     * @param cellIndex índice de la casilla impactada, de 0 a
     *                   {@code type.getSizeInCells() - 1}
     */
    public void markHit(int cellIndex) {
        hitCellIndexes.add(cellIndex);
        redraw();
    }

    /** Marca el barco completo como hundido. */
    public void markSunk() {
        sunk = true;
        redraw();
    }

    private void redraw() {
        getChildren().clear();
        buildShape();
        for (int cellIndex : hitCellIndexes) {
            getChildren().add(buildHitOverlay(cellIndex));
        }
        setOpacity(sunk ? 0.55 : 1.0);
    }

    private Node buildHitOverlay(int cellIndex) {
        double markerSize = thickness() * 0.5;
        double along = cellIndex * cellSize + cellSize / 2 - cellSize * INNER_MARGIN_FACTOR;
        double across = thickness() / 2;

        HitMarkerView marker = new HitMarkerView(markerSize);
        marker.setLayoutX(x(along, across) - markerSize / 2);
        marker.setLayoutY(y(along, across) - markerSize / 2);
        return marker;
    }
}
