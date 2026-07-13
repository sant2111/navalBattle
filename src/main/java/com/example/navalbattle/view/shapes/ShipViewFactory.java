package com.example.navalbattle.view.shapes;

import com.example.navalbattle.model.Orientation;
import com.example.navalbattle.model.ShipType;

/**
 * Patrón: Factory Method (creacional). Cumple requisito del enunciado
 * FPOE Miniproyecto #4.
 * <p>
 * Centraliza la creación de {@link ShipView}: el resto del código (panel
 * de flota, grilla de colocación, vista de previsualización) nunca hace
 * {@code new CarrierView(...)} directamente ni conoce las 4 subclases
 * concretas; solo pide "la vista para este {@code ShipType}" y recibe la
 * silueta correcta ya construida. Si el equipo agrega un tipo de barco
 * nuevo, solo se toca esta fábrica: ningún otro archivo cambia.
 * <p>
 * Nota para la sustentación: en la taxonomía estricta de Gang of Four
 * esto es más precisamente una "Simple Factory" (un único método
 * estático decide qué subclase instanciar), mientras que el "Factory
 * Method" clásico delega esa decisión a una subclase de una clase
 * creadora. Se usa el nombre "Factory Method" porque es como lo pide el
 * enunciado del curso; el efecto que importa —desacoplar al cliente de
 * las clases concretas de barco— es el mismo.
 *
 * @author Jorge Navia
 * @version 1.0
 */
public final class ShipViewFactory {

    private ShipViewFactory() {
    }

    /**
     * Crea la figura 2D correspondiente al tipo de barco dado.
     *
     * @param type        tipo de barco a representar
     * @param cellSize    tamaño en píxeles de una casilla del tablero
     * @param orientation orientación inicial con la que se dibuja
     * @return la silueta ({@link ShipView}) concreta para ese tipo
     */
    public static ShipView createShipView(ShipType type, double cellSize, Orientation orientation) {
        return switch (type) {
            case CARRIER -> new CarrierView(cellSize, orientation);
            case SUBMARINE -> new SubmarineView(cellSize, orientation);
            case DESTROYER -> new DestroyerView(cellSize, orientation);
            case FRIGATE -> new FrigateView(cellSize, orientation);
        };
    }
}
