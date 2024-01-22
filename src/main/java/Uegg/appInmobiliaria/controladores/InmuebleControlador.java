package Uegg.appInmobiliaria.controladores;

import Uegg.appInmobiliaria.entidades.Imagen;
import Uegg.appInmobiliaria.entidades.Inmueble;
import Uegg.appInmobiliaria.enums.Tipo;
import Uegg.appInmobiliaria.excepciones.MyException;
import Uegg.appInmobiliaria.repositorios.InmuebleRepositorio;
import Uegg.appInmobiliaria.servicios.ImagenServicio;
import Uegg.appInmobiliaria.servicios.InmuebleServicio;
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
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/inmueble")
public class InmuebleControlador {

    @Autowired
    private InmuebleServicio inmuebleServicio;

    @Autowired
    private ImagenServicio imagenServicio;

    @Autowired
    private InmuebleRepositorio inmuebleRepositorio;

    @PreAuthorize("hasAnyRole('ROLE_ENTE', 'ROLE_ADMIN')")
    @GetMapping("/crear")
    public String crear(ModelMap modelo) {

        modelo.addAttribute("tipos", Tipo.values());

        return "inmuebleForm.html";
    }

    @PostMapping("/creado")
    public String creado(@RequestParam(required = false) List<MultipartFile> archivos, @RequestParam String idUsuarioEnte, @RequestParam Tipo tipo, @RequestParam String ubicacion, @RequestParam(required = false) Double superficie, @RequestParam(required = false) Integer ambientes,
            @RequestParam String descripcion, @RequestParam(required = false) Double precio, @RequestParam(required = false) String tipoOferta, ModelMap modelo) {

        try {
            if (tipoOferta.equals("venta")) {
                Double precioVenta = precio;
                Double precioAlquiler = null;
                inmuebleServicio.crearInmueble(archivos, idUsuarioEnte, tipo, ubicacion, superficie, ambientes, descripcion, precioVenta, precioAlquiler, tipoOferta);
            } else if (tipoOferta.equals("alquiler")) {
                Double precioAlquiler = precio;
                Double precioVenta = null;
                inmuebleServicio.crearInmueble(archivos, idUsuarioEnte, tipo, ubicacion, superficie, ambientes, descripcion, precioVenta, precioAlquiler, tipoOferta);
            }

            modelo.put("exito", "inmueble creado con exito");

        } catch (MyException ex) {

            modelo.put("error", ex.getMessage());
            modelo.addAttribute("tipos", Tipo.values());
            return "inmuebleForm.html";
        }

        return "redirect:/";

    }

    @GetMapping("/lista/casaAlquiler") //localhost:8080/inmueble/lista/
    public String listarCasaAlquiler(ModelMap modelo) {

        List<Inmueble> inmuebles = inmuebleServicio.listarTipoInmueble(Tipo.CASA, "alquiler");

        modelo.addAttribute("inmuebles", inmuebles);

        return "inmuebleList.html";
    }

    @GetMapping("/lista/departamentoAlquiler") //localhost:8080/inmueble/lista/
    public String listarDepartamentoAlquiler(ModelMap modelo) {

        List<Inmueble> inmuebles = inmuebleServicio.listarTipoInmueble(Tipo.DEPARTAMENTO, "alquiler");

        modelo.addAttribute("inmuebles", inmuebles);

        return "inmuebleList.html";
    }

    @GetMapping("/lista/loteAlquiler") //localhost:8080/inmueble/lista/
    public String listarLoteAlquiler(ModelMap modelo) {

        List<Inmueble> inmuebles = inmuebleServicio.listarTipoInmueble(Tipo.LOTE, "alquiler");

        modelo.addAttribute("inmuebles", inmuebles);

        return "inmuebleList.html";
    }

    @GetMapping("/lista/localAlquiler") //localhost:8080/inmueble/lista/
    public String listarLocalAlquiler(ModelMap modelo) {

        List<Inmueble> inmuebles = inmuebleServicio.listarTipoInmueble(Tipo.LOCAL, "alquiler");

        modelo.addAttribute("inmuebles", inmuebles);

        return "inmuebleList.html";
    }

    @GetMapping("/lista/casaVenta") //localhost:8080/inmueble/lista/
    public String listarCasaVenta(ModelMap modelo) {

        List<Inmueble> inmuebles = inmuebleServicio.listarTipoInmueble(Tipo.CASA, "venta");

        modelo.addAttribute("inmuebles", inmuebles);

        return "inmuebleList.html";
    }

    @GetMapping("/lista/departamentoVenta") //localhost:8080/inmueble/lista/
    public String listarDepartamentoVenta(ModelMap modelo) {

        List<Inmueble> inmuebles = inmuebleServicio.listarTipoInmueble(Tipo.DEPARTAMENTO, "venta");

        modelo.addAttribute("inmuebles", inmuebles);

        return "inmuebleList.html";
    }

    @GetMapping("/lista/localVenta") //localhost:8080/inmueble/lista/
    public String listarLocalVenta(ModelMap modelo) {

        List<Inmueble> inmuebles = inmuebleServicio.listarTipoInmueble(Tipo.LOCAL, "venta");

        modelo.addAttribute("inmuebles", inmuebles);

        return "inmuebleList.html";
    }

    @GetMapping("/lista/loteVenta") //localhost:8080/inmueble/lista/
    public String listarLoteVenta(ModelMap modelo) {

        List<Inmueble> inmuebles = inmuebleServicio.listarTipoInmueble(Tipo.LOTE, "venta");

        modelo.addAttribute("inmuebles", inmuebles);

        return "inmuebleList.html";
    }

    @GetMapping("/detalle/{id}")
    public String detalleInmueble(@PathVariable String id, ModelMap modelo, HttpSession session) {
        Inmueble inmueble = inmuebleRepositorio.getOne(id);

        modelo.addAttribute("inmueble", inmueble);
        modelo.addAttribute("session", session);

        return "inmuebleDetalle.html";
    }

    @GetMapping("/lista/busquedaVenta") //localhost:8080/inmueble/lista/
    public String listarbúsquedaVenta(ModelMap modelo) {

        modelo.addAttribute("tipos", Tipo.values());

        return "busqueda.html";
    }

    @PostMapping("/lista/busquedaVenta") //localhost:8080/inmueble/lista/
    public String búsquedaVenta(@RequestParam Tipo tipo, @RequestParam(required = false) Double superficie,
            @RequestParam(required = false) Integer ambientes, @RequestParam(required = false) Double precio,
            @RequestParam(required = false) Double precioM, ModelMap modelo) {

        List<Inmueble> inmuebles = inmuebleServicio.listarXPVenta(tipo, ambientes, precio, precioM);

        modelo.addAttribute("inmuebles", inmuebles);

        return "inmuebleList.html";
    }

    @GetMapping("/lista/ventasPorPrecio") //localhost:8080/inmueble/lista/
    public String listarbusquedaVentaXP(ModelMap modelo) {

        modelo.addAttribute("tipos", Tipo.values());

        return "ventasPorPrecio.html";
    }

    @PostMapping("/lista/ventasPorPrecio") //localhost:8080/inmueble/lista/
    public String busquedaVentaXP(@RequestParam Tipo tipo,
            @RequestParam(required = false) Integer ambientes, ModelMap modelo) {

        List<Inmueble> inmuebles = inmuebleServicio.listarBusquedaVenta(tipo, ambientes);

        modelo.addAttribute("inmuebles", inmuebles);

        return "inmuebleList.html";
    }

    @GetMapping("/lista/busquedaAlquiler") //localhost:8080/inmueble/lista/
    public String listarbusquedaAlquiler(ModelMap modelo) {

        modelo.addAttribute("tipos", Tipo.values());

        return "busquedaA.html";
    }

    @PostMapping("/lista/busquedaAlquiler") //localhost:8080/inmueble/lista/
    public String busquedaAlquiler(@RequestParam Tipo tipo, @RequestParam(required = false) Double superficie,
            @RequestParam(required = false) Integer ambientes,
            @RequestParam(required = false) Double precio,
            @RequestParam(required = false) Double precioM,
            ModelMap modelo) {

        List<Inmueble> inmuebles = inmuebleServicio.listarXPAlquiler(tipo, ambientes, precio, precioM);

        modelo.addAttribute("inmuebles", inmuebles);

        return "inmuebleList.html";
    }

    @GetMapping("/lista/alquilerPorPrecio") //localhost:8080/inmueble/lista/
    public String listarAlquilerXP(ModelMap modelo) {

        modelo.addAttribute("tipos", Tipo.values());

        return "alquilerPorPrecio.html";
    }

    @PostMapping("/lista/alquilerPorPrecio") //localhost:8080/inmueble/lista/
    public String busquedaAlquilerXP(@RequestParam Tipo tipo,
            @RequestParam(required = false) Integer ambientes,
            ModelMap modelo) {

        List<Inmueble> inmuebles = inmuebleServicio.listarBusquedaAlquiler(tipo, ambientes);

        modelo.addAttribute("inmuebles", inmuebles);

        return "inmuebleList.html";
    }

    @PreAuthorize("hasAnyRole('ROLE_ENTE', 'ROLE_CLIENTE')")
    @GetMapping("/listar/{id}")
    public String listarGeneral(HttpSession session, ModelMap modelo, @PathVariable String id) {

        List<Inmueble> inmuebles = inmuebleServicio.listarInmueblePropietarioYInquilino(id);

        modelo.addAttribute("inmuebles", inmuebles);

        return "inmuebleList.html";
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/listaGeneral") //localhost:8080/inmueble/listaGeneral
    public String listarGeneral(ModelMap modelo) {

        List<Inmueble> inmuebles = inmuebleServicio.listarInmuebles();

        modelo.addAttribute("inmuebles", inmuebles);

        return "inmuebleListAdm.html";
    }

    @GetMapping("/modificar/{id}")
    public String modificarInmueble(@PathVariable String id, ModelMap modelo) {

        modelo.put("inmueble", inmuebleServicio.getOne(id));
        modelo.addAttribute("tipos", Tipo.values());

        return "inmuebleModificar.html";
    }

    @PostMapping("/modificar/{id}")
    public String modificarInmueble(@PathVariable String id, @RequestParam Tipo tipo, @RequestParam String ubicacion, @RequestParam(required = false) Double superficie, @RequestParam(required = false) Integer ambientes,
            @RequestParam String descripcion, @RequestParam(required = false) Double precio, @RequestParam(required = false) String tipoOferta, ModelMap modelo) {

        try {
            if (tipoOferta.equals("venta")) {
                Double precioVenta = precio;
                Double precioAlquiler = null;
                inmuebleServicio.modificarInmueble(id, tipo, ubicacion, superficie, ambientes, descripcion, precioVenta, precioAlquiler, tipoOferta);
            } else if (tipoOferta.equals("alquiler")) {
                Double precioAlquiler = precio;
                Double precioVenta = null;
                inmuebleServicio.modificarInmueble(id, tipo, ubicacion, superficie, ambientes, descripcion, precioVenta, precioAlquiler, tipoOferta);
            }
            modelo.put("exito", "inmueble creado con exito");

            return "redirect:/";

        } catch (MyException ex) {
            modelo.put("error", ex.getMessage());
            modelo.addAttribute("tipos", Tipo.values());

            return "inmuebleModificar.html";
        }

    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable String id, ModelMap modelo) {

        try {
            inmuebleServicio.borrarInmueble(id);
            return "redirect:/"; // Redirige a la lista después de eliminar
        } catch (MyException ex) {
            modelo.put("error", ex.getMessage());
            return "redirect:/"; // Redirige a la lista
        }
    }

    @GetMapping("/listarimagenes/{inmueble_id}")
    public String listarImagenesPorIdInmueble(@PathVariable String inmueble_id, ModelMap modelo) {
        Inmueble inmueble = inmuebleServicio.getOne(inmueble_id);

        modelo.addAttribute("inmueble", inmueble);

        return "InmuebleModificarImagen.html";
    }

    @GetMapping("/eliminar/imagen/{imagen_id}")
    public String eliminarImagenInmueble(@PathVariable String imagen_id, ModelMap modelo) {

        Imagen imagen = imagenServicio.getOne(imagen_id);

        Inmueble inmueble = imagen.getInmueble();

        inmueble.getImagenes().remove(imagen);

        inmuebleRepositorio.save(inmueble);
        return "redirect:/inmueble/listarimagenes/" + inmueble.getId();

    }

    @PostMapping("/anadir/imagen/{inmueble_id}")
    public String anadirImagenInmueble(@PathVariable String inmueble_id, List<MultipartFile> archivos, ModelMap modelo) throws MyException {

        Inmueble inmueble = inmuebleServicio.getOne(inmueble_id);

        List<Imagen> imagenes = inmueble.getImagenes();

        for (MultipartFile archivo : archivos) {
            Imagen imagen = imagenServicio.guardar(archivo);
            inmueble.getImagenes().add(imagen);
        }

        inmuebleRepositorio.save(inmueble);
        return "redirect:/inmueble/listarimagenes/" + inmueble.getId();

    }

}
