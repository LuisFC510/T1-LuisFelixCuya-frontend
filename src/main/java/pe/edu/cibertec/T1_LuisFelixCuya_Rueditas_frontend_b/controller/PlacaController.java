package pe.edu.cibertec.T1_LuisFelixCuya_Rueditas_frontend_b.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import pe.edu.cibertec.T1_LuisFelixCuya_Rueditas_frontend_b.config.RestTemplateConfig;
import pe.edu.cibertec.T1_LuisFelixCuya_Rueditas_frontend_b.dto.PlacaRequestDTO;
import pe.edu.cibertec.T1_LuisFelixCuya_Rueditas_frontend_b.dto.PlacaResponseDTO;
import pe.edu.cibertec.T1_LuisFelixCuya_Rueditas_frontend_b.viewmodel.placaModel;

@Controller
@RequestMapping("/inicio")
public class PlacaController {

    @Autowired
    RestTemplate restTemplate;

    @GetMapping("/placa")
    public String formulario(Model model) {
        model.addAttribute("placaModel", null);
        model.addAttribute("message", null);
        return "inicio";
    }

    @PostMapping("/buscarplaca")
    public String obtenerPlaca(@RequestParam("placa") String placa, Model model){

        // Validación básica de la placa
        if (placa == null || placa.trim().isEmpty() || placa.length() != 7 || !placa.matches("^[a-zA-Z0-9-]+$")) {
            model.addAttribute("message", "Debe ingresar una placa correcta");
            return "inicio";
        }

        try {
            // Simulación de una solicitud HTTP para buscar la placa
            String endpoint = "http://localhost:8081/buscarPlaca/placa";
            PlacaRequestDTO placaRequestDTO = new PlacaRequestDTO(placa);
            PlacaResponseDTO placaResponseDTO = restTemplate.postForObject(endpoint, placaRequestDTO, PlacaResponseDTO.class);

            // Validar que la respuesta no sea nula y que la marca sea válida
            if (placaResponseDTO != null && !"01".equals(placaResponseDTO.Marca())) {
                // Crear un modelo para la vista con los datos obtenidos
                placaModel placaModel = new placaModel(placaResponseDTO.Marca(), placaResponseDTO.Modelo(), placaResponseDTO.NroAsientos(),placaResponseDTO.Precio(), placaResponseDTO.Color());
                model.addAttribute("placaModel", placaModel);

                return "principal";  // Redirige a la vista "buscador" con los datos de la placa
            } else {
                model.addAttribute("message", "No se encontró un vehículo para la placa ingresada");
                return "inicio";  // Redirige a la vista de inicio con el mensaje de error
            }

        } catch (Exception e) {
            model.addAttribute("message", "Ocurrió un problema...");
            System.out.println(e.getMessage());
            return "inicio";  // En caso de error, redirige a la vista de inicio
        }
    }
}
