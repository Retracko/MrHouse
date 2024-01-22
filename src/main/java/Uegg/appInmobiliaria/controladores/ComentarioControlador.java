package Uegg.appInmobiliaria.controladores;

import Uegg.appInmobiliaria.entidades.Comentario;
import Uegg.appInmobiliaria.entidades.Usuario;
import Uegg.appInmobiliaria.repositorios.ComentarioRepositorio;
import Uegg.appInmobiliaria.servicios.ComentarioServicio;
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

@Controller
@RequestMapping("/comentario")
public class ComentarioControlador {

    @Autowired
    ComentarioServicio comentarioServicio;
    @Autowired
    ComentarioRepositorio comentarioRepositorio;

    public String hacerComentario() {
        return "";
    }

    @PreAuthorize("hasAnyRole('ROLE_CLIENTE')")
    @PostMapping("/crear/{idInmueble}")
    public String enviarComentario(@PathVariable String idInmueble, HttpSession session, String comentario) {
        Usuario usuario = (Usuario) session.getAttribute("usuariosession");
        comentarioServicio.crearComentario(idInmueble, usuario.getId(), comentario);
        return "redirec:/";
    }

    @GetMapping("/listar_todo")
    public String listarComentarios(ModelMap modelo) {
        List<Comentario> comentarios = comentarioRepositorio.findAll();
        modelo.addAttribute("comentarios", comentarios);
        return "comentario_list";
    }

    @GetMapping("/listar_imnueble/{id}")
    public String listarComentarioInmueble(@PathVariable String idInmueble, ModelMap modelo) {
        List<Comentario> comentarios = comentarioRepositorio.buscarPorInmueble(idInmueble);
        modelo.addAttribute("comentarios", comentarios);
        return "comentario_list";
    }

    @GetMapping("/listar_usuario/{id}")
    public String listarComentarioUsuario(@PathVariable String idUsuario, ModelMap modelo) {
        List<Comentario> comentarios = comentarioRepositorio.buscarPorUsuario(idUsuario);
        modelo.addAttribute("comentarios", comentarios);
        return "comentario_list";
    }

    @PostMapping("/borrar/{id}")
    public String borrarComentario(@PathVariable String id) {
        comentarioServicio.borrarComentario(id);
        return "redirect:/";
    }
}
