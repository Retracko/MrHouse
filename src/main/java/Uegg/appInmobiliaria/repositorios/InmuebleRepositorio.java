package Uegg.appInmobiliaria.repositorios;

import Uegg.appInmobiliaria.entidades.Inmueble;
import Uegg.appInmobiliaria.enums.Tipo;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface InmuebleRepositorio extends JpaRepository<Inmueble, String> {

    @Query("SELECT l FROM Inmueble l WHERE l.tipo = :tipo AND l.tipoOferta = :tipoOferta AND l.disponibildad =true")
    public List<Inmueble> buscarPorTipo(@Param("tipo") Tipo tipo, @Param("tipoOferta") String tipoOferta);
 
    @Query("SELECT l FROM Inmueble l WHERE l.ambientes = :ambientes AND l.disponibildad =true")
    public List<Inmueble> buscarPorAmbientes(@Param("ambientes") Integer ambientes);

    @Query("SELECT l FROM Inmueble l WHERE l.usuarioPropietario.id = :id AND l.disponibildad =true")
    public List<Inmueble> buscarPorPropietarioYDispo(@Param("id") String id);
    
    @Query("SELECT l FROM Inmueble l WHERE l.usuarioPropietario.id = :id")
    public List<Inmueble> buscarPorProp(@Param("id") String id);
    
    @Query("SELECT l FROM Inmueble l WHERE l.usuarioPropietario.id = :id OR l.usuarioInquilino.id = :id")
    public List<Inmueble> buscarPorPropYInq(@Param("id") String id);

    @Query("SELECT l FROM Inmueble l WHERE l.tipo = :tipo AND l.tipoOferta  LIKE 'venta' AND l.ambientes = :ambientes AND l.disponibildad =true ORDER BY precioVenta")
    public List<Inmueble> busquedaPersonalizadaVenta(@Param("tipo") Tipo tipo, @Param("ambientes") Integer ambientes);

    @Query("SELECT l FROM Inmueble l WHERE l.tipo = :tipo AND l.tipoOferta  LIKE 'alquiler' AND l.ambientes = :ambientes AND l.disponibildad =true ORDER BY precioAlquiler")
    public List<Inmueble> busquedaPersonalizadaAlquiler(@Param("tipo") Tipo tipo, @Param("ambientes") Integer ambientes);

    @Query("SELECT l FROM Inmueble l WHERE l.tipo = :tipo AND l.tipoOferta  LIKE 'alquiler' AND l.ambientes = :ambientes AND l.precioAlquiler <= :precio AND l.precioAlquiler >= :precioM AND l.disponibildad =true")
    public List<Inmueble> busquedaPorPrecioAlquiler(@Param("tipo") Tipo tipo, @Param("ambientes") Integer ambientes, @Param("precio") Double precio, @Param("precioM") Double precioM);

    @Query("SELECT l FROM Inmueble l WHERE l.tipo = :tipo AND l.tipoOferta  LIKE 'venta' AND l.ambientes = :ambientes AND l.precioVenta <= :precio AND l.precioVenta >= :precioM AND l.disponibildad =true")
    public List<Inmueble> busquedaPorPrecioVenta(@Param("tipo") Tipo tipo, @Param("ambientes") Integer ambientes, @Param("precio") Double precio, @Param("precioM") Double precioM);
}
