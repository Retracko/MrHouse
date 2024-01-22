package Uegg.appInmobiliaria.servicios;

import Uegg.appInmobiliaria.entidades.Imagen;
import Uegg.appInmobiliaria.entidades.Inmueble;
import Uegg.appInmobiliaria.entidades.Usuario;
import Uegg.appInmobiliaria.enums.Rol;
import Uegg.appInmobiliaria.excepciones.MyException;
import Uegg.appInmobiliaria.repositorios.ImagenRepositorio;
import Uegg.appInmobiliaria.repositorios.InmuebleRepositorio;
import Uegg.appInmobiliaria.repositorios.UsuarioRepositorio;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Gimenez Victor
 */
@Service
public class UsuarioServicio implements UserDetailsService {

    @Autowired
    private UsuarioRepositorio usuarioRepo;
    @Autowired
    private ImagenServicio imagenServicio;
    @Autowired
    private InmuebleServicio inmuebleServicio;
    @Autowired
    private InmuebleRepositorio inmuebleRepositorio;

    @Transactional
    public void crearCliente(MultipartFile archivo, String denominacion, Long dni, String direccion, Integer codigoPostal, Long telefono,
            String email, String pass, String pass2) throws MyException {

        validarCliente(denominacion, dni, direccion, codigoPostal, telefono, email, pass, pass2);

        Usuario usuario = new Usuario();

        if (archivo.isEmpty()) {
            System.out.println("null : " + archivo);
            Imagen imagen = null;
            usuario.setImagen(imagen);
        } else {
            System.out.println("no null : " + archivo);
            Imagen imagen = imagenServicio.guardar(archivo);
            usuario.setImagen(imagen);
        }

        usuario.setDenominacion(denominacion);
        usuario.setDni(dni);
        usuario.setDireccion(direccion);
        usuario.setCodigoPostal(codigoPostal);
        usuario.setTelefono(telefono);
        usuario.setEmail(email);
        usuario.setPass(new BCryptPasswordEncoder().encode(pass));
        usuario.setRol(Rol.CLIENTE);
        usuario.setFechaAlta(new Date());
        usuario.setActivo(true);
        usuarioRepo.save(usuario);
    }

    @Transactional
    public void crearEnte(MultipartFile archivo, String denominacion, Long cuit, String direccion, Integer codigoPostal, Long telefono,
            String email, String pass, String pass2) throws MyException {
        validarEnte(denominacion, cuit, direccion, codigoPostal, telefono, email, pass, pass2);

        Usuario usuario = new Usuario();

        if (archivo.isEmpty()) {
            System.out.println("null : " + archivo);
            Imagen imagen = null;
            usuario.setImagen(imagen);
        } else {
            System.out.println("no null : " + archivo);
            Imagen imagen = imagenServicio.guardar(archivo);
            usuario.setImagen(imagen);
        }

        usuario.setDenominacion(denominacion);
        usuario.setCuit(cuit);
        usuario.setDireccion(direccion);
        usuario.setCodigoPostal(codigoPostal);
        usuario.setTelefono(telefono);
        usuario.setEmail(email);
        usuario.setPass(new BCryptPasswordEncoder().encode(pass));
        usuario.setRol(Rol.ENTE);
        usuario.setFechaAlta(new Date());
        usuario.setActivo(true);
        usuarioRepo.save(usuario);
    }

    @Transactional
    public void modificarUsuario(
            String id,
            Long telefono,
            String direccion,
            Integer codigoPostal,
            String email,
            String pass,
            String pass2) throws MyException {
        Optional<Usuario> respuesta = usuarioRepo.findById(id);
        if (respuesta.isPresent()) {
            // Obtener la instancia existente
            Usuario usuario = respuesta.get();
            usuario.setTelefono(telefono);
            usuario.setDireccion(direccion);
            usuario.setCodigoPostal(codigoPostal);
            if (pass.equals(pass2)){
                usuario.setPass(new BCryptPasswordEncoder().encode(pass));
            } else {
                throw new MyException("Las contraseñas no coinciden");
            }
            usuarioRepo.save(usuario);
        }
    }

    @Transactional
    public void modificarRol(String id) {
        Optional<Usuario> respuesta = usuarioRepo.findById(id);
        if (respuesta.isPresent()) {
            // Obtener la instancia existente
            Usuario usuario = respuesta.get();
            if (usuario.getRol() == Rol.CLIENTE) {
                usuario.setRol(Rol.ENTE);
            } else {
                usuario.setRol(Rol.CLIENTE);
            }
            usuarioRepo.save(usuario);
        }
    }

    /*
    private Rol asignarRol(String opcion) {
        if (opcion.isEmpty()) {
            return Rol.CLIENTE;
        } else {
            return Rol.ENTE;
        }
    }
    * */
    public List<Usuario> listar() {
        return usuarioRepo.findAll();
    }

    @Transactional
    public void borrar(String id) {
        Optional<Usuario> respuesta = usuarioRepo.findById(id);
        if (respuesta.isPresent()) {
            Usuario usuario = respuesta.get();

            usuarioRepo.delete(usuario);
        }
    }

    @Transactional
    public void baja(String id) {
        Optional<Usuario> respuesta = usuarioRepo.findById(id);
        if (respuesta.isPresent()) {
            //Va a verificar si el usuario tenia inmuebles y tambien les va a dar el alta. 
            List<Inmueble> inmuebles = inmuebleRepositorio.buscarPorProp(id);
            if (inmuebles != null) {
                for (Inmueble inmueble : inmuebles) {
                    inmuebleServicio.NoDisponible(inmueble.getId());
                }
            }

            Usuario usuario = respuesta.get();
            usuario.setActivo(false);
            usuarioRepo.save(usuario);
        }
    }

    @Transactional
    public void alta(String id) {
        Optional<Usuario> respuesta = usuarioRepo.findById(id);
        if (respuesta.isPresent()) {

            //Va a verificar si el usuario tenia inmuebles y tambien les va a dar el alta. 
            List<Inmueble> inmuebles = inmuebleRepositorio.buscarPorProp(id);
            if (inmuebles != null) {
                for (Inmueble inmueble : inmuebles) {
                    inmuebleServicio.Disponible(inmueble.getId());
                }
            }
            // Obtener la instancia existente
            Usuario usuario = respuesta.get();
            usuario.setActivo(true);
            usuarioRepo.save(usuario);
        }
    }

    public void validarCliente(String denominacion, Long dni, String direccion, Integer codigoPostal, Long telefono, String email, String pass, String pass2) throws MyException {
        if (denominacion.isEmpty() || denominacion == null) {
            throw new MyException("El nombre no puede ser nulo.");
        }

        if (dni == null) {
            throw new MyException("El DNI no puede ser nulo.");
        } else if (dni <= 9999999 || dni >= 100000000L) {
            throw new MyException("El DNI debe tener 8 digitos.");
        }

        if (direccion.isEmpty() || direccion == null) {
            throw new MyException("La dirección no puede ser nula.");
        }

        if (codigoPostal == null) {
            throw new MyException("El codigo postal no puede ser nulo.");
        }

        if (telefono == null) {
            throw new MyException("El telefono no puede ser nulo.");
        }

        if (email.isEmpty() || email == null) {
            throw new MyException("El email no puede ser nulo.");
        } else if (email != null) {
            Usuario usuario = usuarioRepo.buscarPorEmail(email);
            if (usuario != null) {
                throw new MyException("El email ya fue registrado.");
            }
        }

        if (pass.isEmpty() || pass == null) {
            throw new MyException("La contraseña no puede ser nula.");
        } else if (pass.length() <= 5) {
            throw new MyException("La contraseña debe tener al menos 6 digitos.");
        }

        if (!pass.equals(pass2)) {
            throw new MyException("Las contraseñas no coinciden.");
        }

    }

    public void validarEnte(String denominacion, Long cuit, String direccion, Integer codigoPostal, Long telefono, String email, String pass, String pass2) throws MyException {
        if (denominacion.isEmpty() || denominacion == null) {
            throw new MyException("La razón social no puede ser nula.");
        }

        if (cuit == null) {
            throw new MyException("El CUIT no puede ser nulo.");
        } else if (cuit <= 9999999999L || cuit >= 100000000000L) {
            throw new MyException("El CUIT debe tener 11 digitos");
        }

        if (direccion.isEmpty() || direccion == null) {
            throw new MyException("La dirección no puede ser nula.");
        }

        if (codigoPostal == null) {
            throw new MyException("El codigo postal no puede ser nulo.");
        }

        if (telefono == null) {
            throw new MyException("El telefono no puede ser nulo.");
        }

        if (email.isEmpty() || email == null) {
            throw new MyException("El email no puede ser nulo.");
        } else if (email != null) {
            Usuario usuario = usuarioRepo.buscarPorEmail(email);
            if (usuario != null) {
                throw new MyException("El email ya fue registrado.");
            }
        }

        if (pass.isEmpty() || pass == null) {
            throw new MyException("La contraseña no puede ser nula.");
        } else if (pass.length() <= 5) {
            throw new MyException("La contraseña debe tener al menos 6 digitos");
        }

        if (!pass.equals(pass2)) {
            throw new MyException("Las contraseñas no coinciden");
        }
    }

    public Usuario getOne(String id) {
        return usuarioRepo.getOne(id);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepo.buscarPorEmail(email);

        if (usuario != null) {

            List<GrantedAuthority> permisos = new ArrayList();

            GrantedAuthority p = new SimpleGrantedAuthority("ROLE_" + usuario.getRol().toString());

            permisos.add(p);

            ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();

            HttpSession session = attr.getRequest().getSession(true);

            session.setAttribute("usuariosession", usuario);

            return new User(usuario.getEmail(), usuario.getPass(), permisos);
        } else {
            return null;
        }
    }
}
