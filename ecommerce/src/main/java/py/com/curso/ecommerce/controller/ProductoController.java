package py.com.curso.ecommerce.controller;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import py.com.curso.ecommerce.model.Producto;
import py.com.curso.ecommerce.model.Usuario;
import py.com.curso.ecommerce.service.ProductoService;
import py.com.curso.ecommerce.service.UploadFileService;
import py.com.curso.ecommerce.service.UsuarioService;

import java.io.IOException;
import java.util.Optional;

@Controller
@RequestMapping("/productos")
@Slf4j
public class ProductoController {
    @Autowired
    private ProductoService service;
    @Autowired
    private UploadFileService uploadFileService;
    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("")
    public String show(Model model) {
        model.addAttribute("productos", service.findAll());
        return "productos/show";
    }

    @GetMapping("/create")
    public String create() {
        return "productos/create";
    }

    @PostMapping("/save")
    public String save(Producto producto, @RequestParam("img") MultipartFile file, HttpSession session) throws IOException {
        log.info("Este es el objeto producto: {}", producto);
        //Usuario usuario = new Usuario();
        //Long identificador = 1L;
        Usuario usuario = usuarioService.findById((Long) session.getAttribute("idUsuario")).get();
        //usuario.setId(identificador);
        usuario.setId(usuario.getId());
        producto.setUsuario(usuario);

        //imagen
        if (producto.getId() == null) { //cuando se crea un nuevo producto
            String nombreImagen = uploadFileService.saveImage(file);
            producto.setImagen(nombreImagen);
        } else {
            /*if (file.isEmpty()) { //cuando editamos el producto pero NO cambiamos la imagen.
                Producto p = new Producto();
                p = service.get(producto.getId()).get();
                producto.setImagen(p.getImagen());
            } else { //cuando editamos el producto y se quiere cambiar tambien la imagen.
                String nombreImagen = uploadFileService.saveImage(file);
                producto.setImagen(nombreImagen);
            }*/
        }

        service.save(producto);
        return "redirect:/productos";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        Producto producto = new Producto();
        Optional<Producto> optionalProducto = service.get(id);
        producto = optionalProducto.get();
        log.info("Producto buscado: {}", producto.getId());

        model.addAttribute("producto", producto);
        //service.save(producto);
        return "/productos/edit";
    }

    @PostMapping("/update")
    public String update(Producto producto, @RequestParam("img") MultipartFile file) throws IOException {

        Producto p = new Producto();
        p = service.get(producto.getId()).get();

        if (file.isEmpty()) { //cuando editamos el producto pero NO cambiamos la imagen.

            producto.setImagen(p.getImagen());
        } else { //cuando editamos el producto y se quiere cambiar tambien la imagen.
            //eliminar imagen
            //Producto p = new Producto();
            //p = service.get(producto.getId()).get();

            //eliminar cuando no sea la imagen por defecto.
            if (!p.getImagen().equals("default.jpg")) {
                uploadFileService.deleteImage(p.getImagen());
            }

            String nombreImagen = uploadFileService.saveImage(file);
            producto.setImagen(nombreImagen);
        }
        producto.setUsuario(p.getUsuario());

        service.update(producto);
        return "redirect:/productos";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        //eliminar imagen
        Producto p = new Producto();
        p = service.get(id).get();

        //eliminar cuando no sea la imagen por defecto.
        if (!p.getImagen().equals("default.jpg")) {
            uploadFileService.deleteImage(p.getImagen());
        }

        service.delete(id);
        return "redirect:/productos";
    }

}
