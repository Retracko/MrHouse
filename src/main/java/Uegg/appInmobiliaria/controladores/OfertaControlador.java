package Uegg.appInmobiliaria.controladores;

import Uegg.appInmobiliaria.entidades.Oferta;
import Uegg.appInmobiliaria.entidades.Usuario;
import Uegg.appInmobiliaria.excepciones.MyException;
import Uegg.appInmobiliaria.repositorios.OfertaRepositorio;
import Uegg.appInmobiliaria.servicios.InmuebleServicio;
import Uegg.appInmobiliaria.servicios.OfertaServicio;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/oferta")
public class OfertaControlador {

    @Autowired
    private OfertaServicio ofertaServicio;
    @Autowired
    private InmuebleServicio inmuebleServicio;

    @PreAuthorize("hasAnyRole('ROLE_CLIENTE', 'ROLE_ENTE')")
    @GetMapping("/crear/{id}")
    public String hacerOferta(
            @PathVariable("id") String id,
            HttpSession session,
            ModelMap modelo
    ) {
        Usuario usuario = (Usuario) session.getAttribute("usuariosession");
        modelo.addAttribute("inmueble", inmuebleServicio.getOne(id));
        modelo.put("usuario", usuario);
        return "oferta_form";
    }

    @PreAuthorize("hasAnyRole('ROLE_CLIENTE')")
    @PostMapping("/enviar")
    public String enviarOferta(
            @RequestParam(required = false) Double montoOferta,
            @RequestParam String tipoOferta,
            @RequestParam("idInmueble") String idInmueble,
            @RequestParam("idCliente") String idCliente,
            ModelMap modelo
    ) throws MyException {

        try {
            ofertaServicio.crearOfertaCliente(montoOferta, tipoOferta, idInmueble, idCliente);

            modelo.put("exito", "Oferta enviada. Espere respuesta del Dueño de la propiedad");
            return "redirect:/inmueble/detalle/" + idInmueble;
        } catch (MyException ex) {
            modelo.put("error", ex.getMessage());
            modelo.put("montoOferta", montoOferta);
            modelo.put("tipoOferta", tipoOferta);
            modelo.put("idInmueble", idInmueble);
            modelo.put("idCliente", idCliente);
            return "redirect:/inmueble/detalle/" + idInmueble;
        }
    }

    @PreAuthorize("hasAnyRole('ROLE_ENTE', 'ROLE_ADMIN')")
    @GetMapping("/inmueble/{idInmueble}")
    public String listarOfertasInmuebles(
            @PathVariable String idInmueble,
            ModelMap modelo
    ) {
        List<Oferta> ofertas = ofertaServicio.listarOfertasInmueble(idInmueble);
        modelo.addAttribute("ofertas", ofertas);
        return "oferta_inmueble_list.html";
    }

    @GetMapping("/aceptar/{id}")
    public String aceptarOferta(@PathVariable String id, ModelMap modelo) {
        Oferta oferta = (Oferta) ofertaServicio.getOne(id);
        String inmueble = oferta.getInmueble().getId();
        ofertaServicio.aceptarOferta(id);
        return "redirect:/oferta/inmueble/" + inmueble;
    }

    @GetMapping("/rechazar/{id}")
    public String rechazarOferta(@PathVariable String id) {
        Oferta oferta = ofertaServicio.getOne(id);
        String inmueble = oferta.getInmueble().getId();
        ofertaServicio.rechazarOferta(id);
        return "redirect:/oferta/inmueble/" + inmueble;
    }

    @PreAuthorize("hasAnyRole('ROLE_CLIENTE', 'ROLE_ENTE', 'ROLE_ADMIN')")
    @GetMapping("/cliente/{idCliente}")
    public String listarOfertasClientes(
            @PathVariable String idCliente,
            ModelMap modelo
    ) {
        List<Oferta> ofertas = ofertaServicio.listarOfertaCliente(idCliente);
        modelo.addAttribute("ofertas", ofertas);
        return "oferta_cliente_list.html";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarOferta(@PathVariable String id) {
        Oferta oferta = (Oferta) ofertaServicio.getOne(id);
        String idUsuario = oferta.getUsuarioCliente().getId();
        ofertaServicio.eliminarOferta(id);
        return "redirect:/oferta/cliente/" + idUsuario;
    }

    @GetMapping("/cancelar/{id}")
    public String cancelar(@PathVariable String id) {
        Oferta oferta = ofertaServicio.getOne(id);
        String idUsuario = oferta.getUsuarioCliente().getId();
        ofertaServicio.cancelarOferta(id);
        return "redirect:/oferta/cliente/" + idUsuario;
    }

    @GetMapping("/confirmar/{id}")
    public String confirmar(@PathVariable String id) {
        Oferta oferta = (Oferta) ofertaServicio.getOne(id);
        String idUsuario = oferta.getUsuarioCliente().getId();
        ofertaServicio.confirmarOferta(id);
        return "redirect:/oferta/cliente/" + idUsuario;
    }

    @GetMapping("/descartar/{id}")
    public String descartar(@PathVariable String id) {
        Oferta oferta = (Oferta) ofertaServicio.getOne(id);
        String idUsuario = oferta.getUsuarioCliente().getId();
        ofertaServicio.descartarOferta(id);
        return "redirect:/oferta/cliente/" + idUsuario;
    }

}
