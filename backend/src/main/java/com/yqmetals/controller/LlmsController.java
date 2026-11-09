package com.yqmetals.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yqmetals.entity.Category;
import com.yqmetals.entity.Product;
import com.yqmetals.service.CategoryService;
import com.yqmetals.service.NewsService;
import com.yqmetals.service.ProductService;
import com.yqmetals.service.SettingService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Machine-readable company profile for llms.txt-aware crawlers.
 * Generated from sys_setting plus the active product catalogue, so it stays
 * consistent with the rendered SPA.
 */
@RestController
@RequestMapping("/api")
public class LlmsController {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Value("${app.base-url}")
    private String baseUrl;

    private final SettingService settingService;
    private final ProductService productService;
    private final NewsService newsService;
    private final CategoryService categoryService;

    public LlmsController(SettingService settingService, ProductService productService,
                          NewsService newsService, CategoryService categoryService) {
        this.settingService = settingService;
        this.productService = productService;
        this.newsService = newsService;
        this.categoryService = categoryService;
    }

    @GetMapping(value = "/llms.txt", produces = "text/plain;charset=UTF-8")
    public ResponseEntity<String> llms() {
        String lastUpdated = settingService.get("last.updated", "");
        if (lastUpdated.isBlank()) {
            lastUpdated = LocalDate.now(ZoneId.of("Asia/Shanghai")).toString();
        }
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .cacheControl(CacheControl.maxAge(Duration.ofSeconds(3600)).cachePublic())
                .header("Content-Disposition", "inline; filename=\"llms.txt\"")
                .body(buildContent(lastUpdated));
    }

    private String buildContent(String lastUpdated) {
        List<String> lines = new ArrayList<>();
        lines.add("# Anping Yuanquan Metals Int'l Co., Ltd. (YQ Metals)");
        lines.add("");
        lines.add("> **Manufacturer and exporter** of steel grating and galvanized steel wire.");
        lines.add("> Founded 2005. Based in Anping County, Hebei Province — the historic centre");
        lines.add("> of China's wire mesh industry. Exporting to 50+ countries.");
        lines.add("");
        lines.add("Last updated: " + lastUpdated);
        lines.add("Sitemap: " + baseUrl + "/sitemap.xml");
        lines.add("Machine-readable endpoints: " + baseUrl + "/api/llms.txt, " + baseUrl + "/api/sitemap.xml, " + baseUrl + "/api/settings/public");
        lines.add("");
        lines.add("## Company profile");
        lines.add("");
        addSetting(lines, "Legal name", "company.name", "Anping Yuanquan Metals Int'l Co., Ltd.");
        addSetting(lines, "Trade name", "company.trade_name", "YQ Metals");
        addSetting(lines, "Founded", "company.founded", "2005");
        addSetting(lines, "Address", "company.address", "No. 229, Chengnan Industrial Park, Anping County, Hebei, China");
        addSetting(lines, "Facility", "company.facility", "20,000 m² production area");
        addSetting(lines, "Markets", "company.markets", "Southeast Asia, Europe, North America and 50+ countries");
        addSetting(lines, "Email", "company.email", "sales@yqwiremesh.com");
        addSetting(lines, "Phone", "company.phone", "");
        lines.add("");
        lines.add("## Products");
        lines.add("");
        addProductTable(lines);
        lines.add("");
        lines.add("## Standards referenced");
        lines.add("");
        lines.add("| Product | Standard | What it controls |");
        lines.add("| --- | --- | --- |");
        lines.add("| Steel Grating | ASTM A36 | Carbon structural steel for load bars and frames |");
        lines.add("| Steel Grating | ASTM A123 | Hot-dip galvanizing of iron and steel products |");
        lines.add("| Steel Grating | BS 4592 | Industrial type flooring and stair treads |");
        lines.add("| Steel Grating | AS 1657 | Fixed platforms, walkways, stairways and ladders |");
        lines.add("| Galvanized Wire | ASTM A153 | Zinc coating on iron and steel hardware |");
        lines.add("| Galvanized Wire | ASTM A641 | Galvanized carbon steel wire |");
        lines.add("");
        lines.add("## Manufacturing capabilities");
        lines.add("");
        lines.add("- CNC cutting and shearing");
        lines.add("- Automated resistance welding");
        lines.add("- Hot-dip galvanizing kettle line");
        lines.add("- In-house quality control and inspection");
        lines.add("- Custom fabrication from drawings or samples");
        lines.add("- Typical lead time: 15–25 days after drawing confirmation");
        lines.add("");
        lines.add("## Quality and certifications");
        lines.add("");
        lines.add("- ISO-certified quality management system");
        lines.add("- Dimensional checks, weld strength tests, coating thickness measurement and visual inspection on every batch");
        lines.add("- Test reports available on request");
        lines.add("");
        lines.add("## Comparisons");
        lines.add("");
        lines.add("### Steel grating vs. checker plate");
        lines.add("");
        lines.add("| Feature | Steel grating | Checker plate |");
        lines.add("| --- | --- | --- |");
        lines.add("| Open area | 40–70%, passes light, air and drainage | Solid, no open area |");
        lines.add("| Weight per m² | Lower for similar span | Higher |");
        lines.add("| Slip resistance | Serrated or knurled bearing bars available | Pattern-embossed surface |");
        lines.add("| Cleaning | Debris falls through | Requires sweeping or washing |");
        lines.add("| Typical use | Walkways, platforms, trench covers, stair treads | Flooring, truck beds, toolboxes |");
        lines.add("");
        lines.add("### Hot-dip galvanized wire vs. electro galvanized wire");
        lines.add("");
        lines.add("| Property | Hot-dip galvanized | Electro galvanized |");
        lines.add("| --- | --- | --- |");
        lines.add("| Coating thickness | Thicker, typically 50–300 g/m² | Thin, typically 5–30 g/m² |");
        lines.add("| Corrosion resistance | Suitable for outdoor and marine exposure | Indoor or short-term use |");
        lines.add("| Appearance | Bright or dull grey, sometimes slightly rough | Smooth, bright and uniform |");
        lines.add("| Coating adhesion | Metallurgical bond, survives bending | Mechanical bond, flakes under heavy deformation |");
        lines.add("| Cost | Higher | Lower |");
        lines.add("");
        lines.add("## Project references");
        lines.add("");
        lines.add("Detailed project cases are maintained in our internal order system and are not published online. Representative project types include industrial plant flooring, offshore platform walkways, power station trench covers, highway drainage grates, construction binding wire and fencing mesh. Project name, location, quantity and year can be provided on request.");
        lines.add("");
        lines.add("## Frequently asked questions");
        lines.add("");
        addFaq(lines, "Where is your factory located?", settingService.get("home.faq.1.a", "No. 229, Chengnan Industrial Park, Anping County, Hebei, China."));
        addFaq(lines, "What products do you manufacture?", settingService.get("home.faq.2.a", "Steel grating and galvanized steel wire for industrial and construction use."));
        addFaq(lines, "How do you control quality?", settingService.get("home.faq.3.a", "Every batch is inspected for dimensions, weld strength, coating thickness and appearance before shipment."));
        addFaq(lines, "Can you support custom specifications?", settingService.get("home.faq.4.a", "Yes. Send your drawing or target specifications and we will confirm size, material, finish and packing."));
        lines.add("");
        lines.add("## Contact");
        lines.add("");
        lines.add("- Email: " + settingService.get("company.email", "sales@yqwiremesh.com"));
        lines.add("- Address: " + settingService.get("company.address", "No. 229, Chengnan Industrial Park, Anping County, Hebei, China"));
        lines.add("");
        lines.add("## Site pages");
        lines.add("");
        lines.add("- Home: " + baseUrl + "/");
        lines.add("- Products: " + baseUrl + "/products");
        lines.add("- Factory: " + baseUrl + "/factory");
        lines.add("- News: " + baseUrl + "/news");
        lines.add("- About and inquiry: " + baseUrl + "/about");
        lines.add("");
        return String.join("\n", lines);
    }

    private void addSetting(List<String> lines, String label, String key, String fallback) {
        String value = settingService.get(key, fallback);
        lines.add("- **" + label + "**: " + (value.isBlank() ? fallback : value));
    }

    private void addFaq(List<String> lines, String question, String answer) {
        lines.add("**" + question + "**");
        lines.add(answer);
        lines.add("");
    }

    private void addProductTable(List<String> lines) {
        List<Product> products = productService.findAllActive();
        List<Category> categories = categoryService.findByType("PRODUCT");
        Map<Long, Category> byId = new HashMap<>();
        for (Category c : categories) {
            if (c.getId() != null) byId.put(c.getId(), c);
        }

        lines.add("| Product | Category | Key specifications |");
        lines.add("| --- | --- | --- |");
        for (Product p : products) {
            String specs = formatSpecs(p, byId.get(p.getCategoryId()));
            lines.add("| " + escapeMarkdown(p.getName()) + " | " + escapeMarkdown(categoryName(p, byId)) + " | " + specs + " |");
        }
    }

    private String categoryName(Product product, Map<Long, Category> byId) {
        Category category = byId.get(product.getCategoryId());
        return category != null ? category.getNameEn() : "Unknown";
    }

    private String formatSpecs(Product product, Category category) {
        if (category == null || product.getSpecs() == null) {
            return escapeMarkdown(product.getDescription() == null ? "" : product.getDescription().replace("\n", ", "));
        }
        List<Param> params;
        try {
            params = MAPPER.readValue(category.getSpecs(), new TypeReference<>() {});
        } catch (Exception e) {
            return escapeMarkdown(product.getDescription() == null ? "" : product.getDescription().replace("\n", ", "));
        }
        if (params == null || params.isEmpty()) {
            return escapeMarkdown(product.getDescription() == null ? "" : product.getDescription().replace("\n", ", "));
        }
        Map<String, String> values = new HashMap<>();
        try {
            JsonNode node = MAPPER.readTree(product.getSpecs());
            if (node.isObject()) {
                node.fields().forEachRemaining(entry -> values.put(entry.getKey(), entry.getValue().asText()));
            }
        } catch (Exception e) {
            // Ignore malformed specs and fall back to the description.
        }
        List<String> rows = new ArrayList<>();
        for (Param param : params) {
            if (param.getName() == null) continue;
            String value = values.get(String.valueOf(param.getId()));
            if (value != null && !value.isBlank()) {
                String unit = param.getUnit();
                if (unit != null && !unit.isBlank() && !value.toLowerCase().contains(unit.toLowerCase())) {
                    value = value + " " + unit;
                }
                rows.add(escapeMarkdown(param.getName()) + ": " + escapeMarkdown(value));
            }
        }
        return rows.isEmpty() ? "" : String.join(", ", rows);
    }

    private static String escapeMarkdown(String value) {
        if (value == null) return "";
        return value.replace("|", "\\|");
    }

    /**
     * Parameter shape stored in category.specs.
     */
    @lombok.Data
    private static class Param {
        private Long id;
        private String name;
        private String unit;
    }
}
