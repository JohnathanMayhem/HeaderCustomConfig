package com.example.headconfig;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Базовый класс для тестов, чтобы не дублировать @RestController
@SpringBootTest(classes = {CustomHeadConfigTest.TestApplication.class, CustomHeadConfig.class})
@AutoConfigureMockMvc
class CustomHeadConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ApplicationContext context;

    // Минимальное веб-приложение для тестов
    @Configuration
    static class TestApplication {
        @RestController
        static class TestController {
            @GetMapping("/test")
            public String testEndpoint() {
                return "OK";
            }
        }
    }

    @Test
    void whenHeaderIsEnabledByDefault_thenHeaderIsPresent() throws Exception {

        mockMvc.perform(get("/test"))
                .andExpect(status().isOk())
                .andExpect(header().string("Foo", "Bar"));

        FilterRegistrationBean<?> filterRegBean = context.getBean("fooBarHeaderFilterRegistrationBean", FilterRegistrationBean.class);
        assertThat(filterRegBean).isNotNull();
        assertThat(filterRegBean.getFilter()).isInstanceOf(FooBarHeaderFilter.class);
        System.out.println("Test (Default enabled): FooBarHeaderFilterRegistrationBean found.");
    }

    // Тест включенным свойством
    @SpringBootTest(classes = {CustomHeadConfigTest.TestApplication.class, CustomHeadConfig.class})
    @AutoConfigureMockMvc
    @TestPropertySource(properties = "custom.http.header.enabled=true")
    static class EnabledHeaderTest {
        @Autowired
        private MockMvc mockMvc;
        @Autowired
        private ApplicationContext context;

        @Test
        void whenHeaderIsEnabledExplicitly_thenHeaderIsPresent() throws Exception {
            mockMvc.perform(get("/test"))
                    .andExpect(status().isOk())
                    .andExpect(header().string("Foo", "Bar"));

            FilterRegistrationBean<?> filterRegBean = context.getBean("fooBarHeaderFilterRegistrationBean", FilterRegistrationBean.class);
            assertThat(filterRegBean).isNotNull();
            assertThat(filterRegBean.getFilter()).isInstanceOf(FooBarHeaderFilter.class);
            System.out.println("Test (Explicitly enabled): FooBarHeaderFilterRegistrationBean found.");
        }
    }

    // Вложенный класс для теста с выключенным свойством
    @SpringBootTest(classes = {CustomHeadConfigTest.TestApplication.class, CustomHeadConfig.class})
    @AutoConfigureMockMvc
    @TestPropertySource(properties = "custom.http.header.enabled=false")
    static class DisabledHeaderTest {
        @Autowired
        private MockMvc mockMvc;
        @Autowired
        private ApplicationContext context;

        @Test
        void whenHeaderIsDisabled_thenHeaderIsNotPresentAndBeanIsMissing() throws Exception {
            mockMvc.perform(get("/test"))
                    .andExpect(status().isOk())
                    .andExpect(header().doesNotExist("Foo"));

            assertThatThrownBy(() -> context.getBean("fooBarHeaderFilterRegistrationBean", FilterRegistrationBean.class))
                    .isInstanceOf(NoSuchBeanDefinitionException.class);
            System.out.println("Test (Disabled): FooBarHeaderFilterRegistrationBean NOT found, as expected.");

        }
    }
}