package com.rikkeibank.customer.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.rikkeibank.customer.service.CustomerService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class CustomerControllerTest {
    @Test
    void listEndpointReturnsOk() throws Exception {
        CustomerService service = mock(CustomerService.class);
        when(service.findAll()).thenReturn(List.of());
        MockMvc mvc = MockMvcBuilders.standaloneSetup(new CustomerController(service)).build();
        mvc.perform(get("/api/customers")).andExpect(status().isOk());
    }
}
