package Uegg.appInmobiliaria.controladores;

import Uegg.appInmobiliaria.entidades.Usuario;
import Uegg.appInmobiliaria.excepciones.MyException;
import Uegg.appInmobiliaria.servicios.UsuarioServicio;
import java.util.List;
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
@RequestMapping("/usuario")
public class UsuarioControlador {

    @Autowired
    private UsuarioServicio usuarioServicio;

    @GetMapping("/crearCliente")
    public String crearCliente() {

        return "cliente_form.html";
    }

    @PostMapping("/creadoCliente")
    public String creadoCliente(
            @RequestParam(required = false) MultipartFile archivo,
            @RequestParam String denominacion,
            @RequestParam(required = false) Long dni,
            @RequestParam String direccion,
            @RequestParam(required = false) Integer codigoPostal,
            @RequestParam(required = false) Long telefono,
            @RequestParam String email,
            @RequestParam String pass,
            @RequestParam String pass2,
            ModelMap modelo
    ) throws MyException {
        try {

            usuarioServicio.crearCliente(archivo, denominacion, dni, direccion, codigoPostal, telefono, email, pass, pass2);
            modelo.put("exito", "Usuario registrado con éxito");
            return "login.html";

        } catch (MyException e) {
            modelo.put("error", e.getMessage());
            modelo.put("denominacion", denominacion);
            modelo.put("dni", dni);
            modelo.put("direccion", direccion);
            modelo.put("codigoPostal", codigoPostal);
            modelo.put("telefono", telefono);
            modelo.put("email", email);
            modelo.put("pass", pass);
            modelo.put("pass2", pass2);
            return "cliente_form.html";
        }
    }

    @GetMapping("/crearEnte")
    public String crearEnte() {

        return "ente_form.html";
    }

    @PostMapping("/creadoEnte")
    public String creadoEnte(
            @RequestParam(required = false) MultipartFile archivo,
            @RequestParam String denominacion,
            @RequestParam(required = false) Long cuit,
            @RequestParam String direccion,
            @RequestParam(required = false) Integer codigoPostal,
            @RequestParam(required = false) Long telefono,
            @RequestParam String email,
            @RequestParam String pass,
            @RequestParam String pass2,
            ModelMap modelo
    ) throws MyException {
        try {

            usuarioServicio.crearEnte(archivo, denominacion, cuit, direccion, codigoPostal, telefono, email, pass, pass2);
            modelo.put("exito", "Usuario registrado con éxito");
            return "login.html";

        } catch (MyException e) {
            modelo.put("error", e.getMessage());
            modelo.put("denominacion", denominacion);
            modelo.put("cuit", cuit);
            modelo.put("direccion", direccion);
            modelo.put("codigoPostal", codigoPostal);
            modelo.put("telefono", telefono);
            modelo.put("email", email);
            modelo.put("pass", pass);
            modelo.put("pass2", pass2);
            return "ente_form.html";
        }
    }

    @PreAuthorize("hasAnyRole('ROLE_ENTE', 'ROLE_ADMIN')")
    @GetMapping("/lista")
    public String lista(ModelMap modelo) {
        List<Usuario> usuarios = usuarioServicio.listar();
        modelo.put("usuarios", usuarios);
        return "usuario_list.html";
    }

    @GetMapping("/modificar/{id}")
    public String modificar(@PathVariable String id) {

        return "usuario_modificar.html";
    }

    @PostMapping("/modificar/{id}")
    public String modificar(
            @PathVariable String id,
            @RequestParam(required = false) Long telefono,
            @RequestParam String direccion,
            @RequestParam(required = false) Integer codigoPostal,
            @RequestParam String email,
            @RequestParam String pass,
            @RequestParam String pass2,
            ModelMap modelo
    ) {
        try {
            usuarioServicio.modificarUsuario(id, telefono, direccion, codigoPostal, email, pass, pass2);
            modelo.put("exito", "Usuario actualizado correctamente!");
            return "login.html";
        } catch (Exception ex) {
            modelo.put("error", ex.getMessage());
            return "usuario_modificar.html";
        }

    }

    @GetMapping("/perfil/{id}")
    public String perfil(@PathVariable String id, ModelMap modelo) {
        Usuario usuario = usuarioServicio.getOne(id);

        modelo.addAttribute("usuario", usuario);

        return "perfil.html";
    }
}
