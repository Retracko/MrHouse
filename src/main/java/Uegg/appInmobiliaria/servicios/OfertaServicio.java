package Uegg.appInmobiliaria.servicios;

import Uegg.appInmobiliaria.entidades.Inmueble;
import Uegg.appInmobiliaria.entidades.Oferta;
import Uegg.appInmobiliaria.entidades.Usuario;
import Uegg.appInmobiliaria.excepciones.MyException;
import Uegg.appInmobiliaria.repositorios.InmuebleRepositorio;
import Uegg.appInmobiliaria.repositorios.OfertaRepositorio;
import Uegg.appInmobiliaria.repositorios.UsuarioRepositorio;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OfertaServicio {

    @Autowired
    private OfertaRepositorio ofertaRepositorio;
    @Autowired
    private UsuarioRepositorio usuarioRepositorio;
    @Autowired
    private InmuebleRepositorio inmuebleRepositorio;
    @Autowired
    private InmuebleServicio inmuebleServicio;

    @Transactional
    public void crearOfertaCliente(Double monto, String tipoOferta, String idInmueble, String idCliente) throws MyException {
        validar(monto);
        Inmueble inmueble = inmuebleRepositorio.getOne(idInmueble);
        Usuario usuario = usuarioRepositorio.getOne(idCliente);
        Oferta oferta = new Oferta();
        oferta.setUsuarioCliente(usuario);
        oferta.setMontoOferta(monto);
        oferta.setTipoOferta(tipoOferta);
        oferta.setInmueble(inmueble);
        oferta.setFechaOferta(new Date());
        oferta.setEstadoOferta("PENDIENTE");
        oferta.setVigente(true);
        ofertaRepositorio.save(oferta);
    }

    @Transactional
    public void eliminarOferta(String id) {
        Oferta oferta = ofertaRepositorio.getOne(id);
        ofertaRepositorio.delete(oferta);
    }

    @Transactional
    public void aceptarOferta(String idOferta) {
        Oferta oferta = ofertaRepositorio.getOne(idOferta);
        oferta.setEstadoOferta("ACEPTADA");
        oferta.setVigente(false);
        Inmueble inmueble = oferta.getInmueble();
        inmueble.setDisponibildad(false);
        inmuebleRepositorio.save(inmueble);
        ofertaRepositorio.save(oferta);
        rechazarOfertas(inmueble.getId());
    }

    //CANCELACION
    @Transactional
    public void cancelarOferta(String id) {
        Oferta oferta = ofertaRepositorio.getOne(id);
        oferta.setEstadoOferta("CANCELACION");
        ofertaRepositorio.save(oferta);

    }

    @Transactional
    public void confirmarOferta(String id) {
        Oferta oferta = ofertaRepositorio.getOne(id);
        Inmueble inmueble = oferta.getInmueble();
        if (oferta.getEstadoOferta().equalsIgnoreCase("ACEPTADA")) {
            if (oferta.getTipoOferta().equalsIgnoreCase("VENTA")) {
                inmueble.setUsuarioPropietario(oferta.getUsuarioCliente());
                inmuebleRepositorio.save(inmueble);
            } else {
                inmueble.setUsuarioInquilino(oferta.getUsuarioCliente());
                inmuebleRepositorio.save(inmueble);
            }
            oferta.setEstadoOferta("CONFIRMADA");
            ofertaRepositorio.save(oferta);
        } else if (oferta.getEstadoOferta().equalsIgnoreCase("CANCELACION")) {
            oferta.setEstadoOferta("CANCELADA");
            oferta.setVigente(false);
            inmueble.setUsuarioInquilino(null);
            inmueble.setDisponibildad(true);
            inmuebleRepositorio.save(inmueble);
            ofertaRepositorio.save(oferta);
        }
    }

    @Transactional
    public void descartarOferta(String id) {
        Oferta oferta = ofertaRepositorio.getOne(id);
        if (oferta.getEstadoOferta().equalsIgnoreCase("ACEPTADA")) {
            Inmueble inmueble = oferta.getInmueble();
            inmueble.setDisponibildad(Boolean.TRUE);
            inmuebleRepositorio.save(inmueble);

            oferta.setEstadoOferta("DESCARTADA");
            ofertaRepositorio.save(oferta);
        }
    }

    @Transactional
    public void rechazarOferta(String id) {
        Optional<Oferta> respuesta = ofertaRepositorio.findById(id);
        if (respuesta.isPresent()) {
            Oferta oferta = respuesta.get();
            oferta.setEstadoOferta("RECHAZADA");
            oferta.setVigente(false);
            ofertaRepositorio.save(oferta);
        }
    }

    @Transactional
    private void rechazarOfertas(String idInmueble) {
        List<Oferta> ofertas = ofertaRepositorio.buscarPorInmueble(idInmueble);
        for (Oferta oferta : ofertas) {
            if (oferta.getEstadoOferta().equalsIgnoreCase("PENDIENTE")) {
                oferta.setEstadoOferta("RECHAZADA");
                oferta.setVigente(false);
                ofertaRepositorio.save(oferta);
            }
        }
    }

    public List<Oferta> listarOfertaCliente(String id) {
        return ofertaRepositorio.buscarPorCliente(id);
    }

    public List<Oferta> listarOfertasInmueble(String id) {
        return ofertaRepositorio.buscarPorInmueble(id);
    }

    public Oferta mejorOferta(String id) {
        return ofertaRepositorio.buscarOfertaMayor(id);
    }

    public Oferta contarOfertas(String id) {
        return ofertaRepositorio.contarOfertasPorInmueble(id);
    }

    public Oferta getOne(String idOferta) {
        return ofertaRepositorio.getOne(idOferta);
    }

    public void validar(Double montoOferta) throws MyException {
        if (montoOferta <= 0 || montoOferta == null) {
            throw new MyException("La oferta no puede ser vacia o  ser menor a cero.");
        }
    }
}
