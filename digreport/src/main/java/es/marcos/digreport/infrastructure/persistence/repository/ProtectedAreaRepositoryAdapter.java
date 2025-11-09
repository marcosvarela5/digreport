package es.marcos.digreport.infrastructure.persistence.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.marcos.digreport.application.port.out.ProtectedAreaRepositoryPort;
import es.marcos.digreport.domain.enums.ProtectedAreaType;
import es.marcos.digreport.domain.model.ProtectedArea;
import es.marcos.digreport.infrastructure.persistence.entities.ProtectedAreaEntityJpa;
import es.marcos.digreport.infrastructure.persistence.mapper.ProtectedAreaMapper;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public class ProtectedAreaRepositoryAdapter implements ProtectedAreaRepositoryPort {

    private final SpringDataProtectedAreaRepository repository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ProtectedAreaRepositoryAdapter(SpringDataProtectedAreaRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public ProtectedArea save(ProtectedArea area) {
        ProtectedAreaEntityJpa entity = ProtectedAreaMapper.toEntity(area);
        ProtectedAreaEntityJpa saved = repository.save(entity);
        return ProtectedAreaMapper.toDomain(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ProtectedArea> findById(Long id) {
        return repository.findById(id).map(ProtectedAreaMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProtectedArea> findAll() {
        return repository.findAll()
                .stream()
                .map(ProtectedAreaMapper::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProtectedArea> findAllActive() {
        return repository.findByActiveTrue()
                .stream()
                .map(ProtectedAreaMapper::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProtectedArea> findByCcaa(String ccaa) {
        return repository.findByCcaa(ccaa)
                .stream()
                .map(ProtectedAreaMapper::toDomain)
                .toList();
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByCoordinates(double latitude, double longitude) {
        List<ProtectedArea> areas = findAllActive();

        for (ProtectedArea area : areas) {
            try {
                if (area.getType() == ProtectedAreaType.MONUMENT) {

                    if (isNearMonument(area.getGeometry(), latitude, longitude)) {
                        return true;
                    }
                } else if (area.getType() == ProtectedAreaType.AREA) {

                    if (isInsidePolygon(area.getGeometry(), latitude, longitude)) {
                        return true;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    private boolean isNearMonument(String geoJson, double lat, double lon) throws Exception {
        JsonNode geometry = objectMapper.readTree(geoJson);


        JsonNode coordinates = geometry.get("coordinates");
        double monumentLon = coordinates.get(0).asDouble();
        double monumentLat = coordinates.get(1).asDouble();


        double distance = calculateDistance(lat, lon, monumentLat, monumentLon);


        return distance < 100;
    }

    /**
     * Verifica si un punto está dentro de un polígono usando el algoritmo Ray Casting
     */
    private boolean isInsidePolygon(String geoJson, double lat, double lon) throws Exception {
        JsonNode geometry = objectMapper.readTree(geoJson);


        JsonNode coordinates = geometry.get("coordinates").get(0);

        int numVertices = coordinates.size();
        boolean inside = false;

        for (int i = 0, j = numVertices - 1; i < numVertices; j = i++) {
            double xi = coordinates.get(i).get(0).asDouble();
            double yi = coordinates.get(i).get(1).asDouble();
            double xj = coordinates.get(j).get(0).asDouble();
            double yj = coordinates.get(j).get(1).asDouble();

            boolean intersect = ((yi > lat) != (yj > lat))
                    && (lon < (xj - xi) * (lat - yi) / (yj - yi) + xi);

            if (intersect) {
                inside = !inside;
            }
        }

        return inside;
    }

    /**
     * Calcula la distancia entre dos puntos en metros usando la fórmula de Haversine
     */
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int EARTH_RADIUS = 6371;

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS * c * 1000;
    }
}
