import type { RouteLocationNormalized } from "vue-router";

const BASE_URL = "https://www.yqmetals.com";
const HERO_IMAGE = "/uploads/545dd1ab-3e26-4c18-a624-c24002a1bb59.png";

// Approximate coordinates of the factory in Anping County, Hebei.
const ANPING = { latitude: 38.23, longitude: 115.67 };

// Route-specific hero image, used for og:image and twitter:image.
const ROUTE_IMAGE: Record<string, string | undefined> = {
  Home: BASE_URL + HERO_IMAGE,
  About: BASE_URL + HERO_IMAGE,
};

const PAGE_META: Record<string, { title: string; description: string }> = {
  Home: {
    title: "Anping Yuanquan Metals Int'l Co., Ltd. | Steel Grating & Galvanized Wire",
    description: "China-based manufacturer and exporter of steel grating and galvanized steel wire. Serving customers worldwide since 2005. ISO-certified quality, OEM/ODM support.",
  },
  Products: {
    title: "Products | Steel Grating & Wire Mesh - YQ Metals",
    description: "Browse our full product range: steel grating and galvanized steel wire. Custom sizes and specifications available.",
  },
  ProductDetail: {
    title: "Product Details - YQ Metals",
    description: "View detailed specifications for our industrial metal products.",
  },
  Factory: {
    title: "Our Factory | Manufacturing Facility Tour - YQ Metals",
    description: "Tour our 20,000 m2 production facility No. 229, Chengnan Industrial Park, Anping County, Hebei. CNC cutting, automated welding, and hot-dip galvanizing capabilities.",
  },
  News: {
    title: "News & Insights | Industry Updates - YQ Metals",
    description: "Latest company news, industry trends, and exhibition highlights from Anping Yuanquan Metals.",
  },
  NewsDetail: {
    title: "News Article - YQ Metals",
    description: "Company update and industry insights from YQ Metals.",
  },
  About: {
    title: "About Us | YQ Metals - Steel Grating Manufacturer Since 2005",
    description: "Learn about Anping Yuanquan Metals Int'l Co., Ltd. - a professional steel grating and wire mesh manufacturer and exporter based in Anping, China.",
  },
};

// Breadcrumb labels that resolve to a fixed path; the last trail item always
// uses the current route.
const CRUMB_PATHS: Record<string, string> = {
  Home: "/",
  Products: "/products",
  ProductDetail: "/products",
  Factory: "/factory",
  News: "/news",
  NewsDetail: "/news",
  About: "/about",
};

function baseUrl(): string {
  return (typeof window !== "undefined" && window.location.origin) || BASE_URL;
}

// Mirrors the two helpers in ProductDetail.vue so the JSON-Ld values read
// exactly like the specification table shown to the visitor.
function parseSpecs(value: any): any {
  if (!value) return null;
  if (typeof value !== "string") return value;
  try {
    return JSON.parse(value);
  } catch {
    return null;
  }
}

function formatSpecValue(value: any, unit?: string): string {
  if (value === undefined || value === null || value === "") return "";
  const text = String(value).trim();
  if (!unit || !text) return text;
  return text.toLowerCase().includes(String(unit).toLowerCase()) ? text : `${text} ${unit}`;
}

function routeCrumbs(name: string): string[] {
  switch (name) {
    case "Products":
      return ["Home", "Products"];
    case "Factory":
      return ["Home", "Factory"];
    case "News":
      return ["Home", "News"];
    default:
      return ["Home"];
  }
}

export function organization(settings?: Record<string, string>): Record<string, unknown> {
  const base = baseUrl();
  const name = settings?.["company.name"] || "Anping Yuanquan Metals Int'l Co., Ltd.";
  const alternateName = settings?.["company.trading.name"] || "YQ Metals";
  const phone = settings?.["company.phone"];
  return {
    "@context": "https://schema.org",
    "@type": "Organization",
    name,
    alternateName,
    url: base + "/",
    description:
      "Manufacturer and exporter of steel grating and galvanized steel wire based No. 229, Chengnan Industrial Park, Anping County, Hebei, China. Serving customers worldwide since 2005.",
    email: "sales@yqwiremesh.com",
    foundingDate: "2005",
    address: {
      "@type": "PostalAddress",
      streetAddress: "No. 229, Chengnan Industrial Park",
      addressLocality: "Anping County",
      addressRegion: "Hebei",
      addressCountry: "CN",
    },
    logo: base + HERO_IMAGE,
    sameAs: ["https://www.linkedin.com/company/yqmetals"],
    ...(phone ? { telephone: phone } : {}),
    ...(settings?.["company.certifications"]
      ? { award: settings["company.certifications"].split(",").map((s) => s.trim()).filter(Boolean) }
      : {}),
    ...(settings?.["company.annual.capacity"] || settings?.["company.factory.area"]
      ? { additionalProperty: [
          ...(settings?.["company.annual.capacity"]
            ? [{ "@type": "PropertyValue", name: "Annual production capacity", value: settings["company.annual.capacity"] }]
            : []),
          ...(settings?.["company.factory.area"]
            ? [{ "@type": "PropertyValue", name: "Factory area", value: settings["company.factory.area"] }]
            : []),
        ]}
      : {}),
  };
}

export function localBusiness(settings?: Record<string, string>): Record<string, unknown> {
  const base = baseUrl();
  const org = organization(settings);
  return {
    "@context": "https://schema.org",
    "@type": "LocalBusiness",
    name: org.name,
    alternateName: org.alternateName,
    url: base + "/",
    email: org.email,
    address: org.address,
    geo: { "@type": "GeoCoordinates", latitude: ANPING.latitude, longitude: ANPING.longitude },
    sameAs: org.sameAs,
    openingHoursSpecification: {
      "@type": "OpeningHoursSpecification",
      dayOfWeek: ["Monday","Tuesday","Wednesday","Thursday","Friday"],
      opens: "09:00",
      closes: "18:00",
    },
  };
}

export function breadcrumb(trail: string[], routePath: string): Record<string, unknown> {
  const base = baseUrl();
  const elements = trail.map((label, i) => {
    const path = i === trail.length - 1 ? routePath : CRUMB_PATHS[label] || "/";
    return { "@type": "ListItem", position: i + 1, name: label, item: base + path };
  });
  return { "@context": "https://schema.org", "@type": "BreadcrumbList", itemListElement: elements };
}

export function faqPage(faqs: { q: string; a: string }[]): Record<string, unknown> {
  return {
    "@context": "https://schema.org",
    "@type": "FAQPage",
    mainEntity: faqs.map((f) => ({
      "@type": "Question",
      name: f.q,
      acceptedAnswer: { "@type": "Answer", text: f.a },
    })),
  };
}

export function productSchema(product: any, category: any): Record<string, unknown>[] {
  const base = baseUrl();
  const defs: any[] = (parseSpecs(category?.specs) as any[]) || [];
  const defMap = new Map<string, any>();
  defs.forEach((d: any) => {
    if (d && d.id != null) defMap.set(String(d.id), d);
  });
  const raw: any = parseSpecs(product.specs);
  const additional: unknown[] = [];
  if (raw && typeof raw === "object") {
    Object.entries(raw).forEach(([key, value]: [string, any]) => {
      const def = defMap.get(key) || {};
      const text = formatSpecValue(value, def.unit);
      if (def.name && text) {
        additional.push({ "@type": "PropertyValue", name: def.name, value: text, unitText: def.unit });
      }
    });
  }
  const image = product.imageUrl ? base + product.imageUrl : undefined;
  return [
    {
      "@context": "https://schema.org",
      "@type": "Product",
      name: product.name,
      description: product.description,
      ...(image ? { image } : {}),
      manufacturer: { "@type": "Organization", name: "Anping Yuanquan Metals Int'l Co., Ltd.", url: base + "/" },
      ...(additional.length ? { additionalProperty: additional } : {}),
    },
    breadcrumb(["Home", "Products", product.name], `/product/${product.id}`),
  ];
}

export function newsArticle(news: any): Record<string, unknown>[] {
  const base = baseUrl();
  const isoDate = (v: string | undefined): string | undefined => (v ? new Date(v).toISOString().split("T")[0] : undefined);
  const published = isoDate(news.publishDate);
  const image = news.coverImageUrl ? base + news.coverImageUrl : undefined;
  return [
    {
      "@context": "https://schema.org",
      "@type": "NewsArticle",
      headline: news.title,
      description: news.summary,
      datePublished: published,
      dateModified: isoDate(news.updateTime) || published,
      author: { "@type": "Organization", name: "Anping Yuanquan Metals Int'l Co., Ltd." },
      publisher: {
        "@type": "Organization",
        name: "Anping Yuanquan Metals Int'l Co., Ltd.",
        logo: { "@type": "ImageObject", url: base + HERO_IMAGE },
      },
      mainEntityOfPage: { "@type": "WebPage", "@id": base + `/news/${news.id}` },
      ...(image ? { image } : {}),
      articleBody: toPlainText(news.content),
    },
    breadcrumb(["Home", "News", news.title], `/news/${news.id}`),
  ];
}

// Injects or replaces the single <script type="application/ld+json"> block.
// Passing null removes it, which is how a page component takes over from the
// generic schema installed by the router guard.
export function setJsonLd(
  schema: Record<string, unknown> | Record<string, unknown>[] | null,
): void {
  const existing = document.head.querySelector('script[type="application/ld+json"]');
  if (existing) existing.remove();
  if (!schema) return;
  const el = document.createElement("script");
  el.type = "application/ld+json";
  el.textContent = Array.isArray(schema) ? JSON.stringify(schema) : JSON.stringify(schema);
  document.head.appendChild(el);
}

// News content is stored as rich HTML, but Schema.org articleBody must be
// plain text. Strip tags and decode the most common HTML entities.
export function toPlainText(html: unknown): string {
  if (typeof html !== "string") return "";

  if (typeof DOMParser !== "undefined") {
    try {
      const text = new DOMParser().parseFromString(html, "text/html").body.textContent;
      return text ? text.replace(/\s+/g, " ").trim() : "";
    } catch {
      // Fall through to the regex path if the browser parser rejects the input.
    }
  }

  return html
    .replace(/<!--[\s\S]*?-->/g, "")
    .replace(/<script[^>]*>[\s\S]*?<\/script>/gi, "")
    .replace(/<style[^>]*>[\s\S]*?<\/style>/gi, "")
    .replace(/<[^>]+>/g, "")
    .replace(/&nbsp;/gi, " ")
    .replace(/&amp;/gi, "&")
    .replace(/&lt;/gi, "<")
    .replace(/&gt;/gi, ">")
    .replace(/&quot;/gi, '"')
    .replace(/&#39;/gi, "'")
    .replace(/\s+/g, " ")
    .trim();
}

export function useMeta() {
  function updateMeta(route: RouteLocationNormalized) {
    const name = route.name as string;
    const meta = PAGE_META[name];
    if (!meta) return;
    document.title = meta.title;
    setMeta("description", meta.description);
    setMeta("keywords", "steel grating manufacturer, galvanized steel wire, hot dip galvanized grating, wire mesh supplier China, industrial metal products, Anping Yuanquan Metals");
    setMeta("og:title", meta.title, "property");
    setMeta("og:description", meta.description, "property");
    setMeta("og:type", "website", "property");
    const image = ROUTE_IMAGE[name];
    if (image) {
      setMeta("og:image", image, "property");
      setMeta("twitter:image", image, "name");
    }
    let canonical = document.querySelector<HTMLLinkElement>('link[rel="canonical"]');
    if (!canonical) {
      canonical = document.createElement("link");
      canonical.rel = "canonical";
      document.head.appendChild(canonical);
    }
    canonical.href = window.location.origin + route.path;
    setMeta("twitter:title", meta.title, "name");
    setMeta("twitter:description", meta.description, "name");
    setMeta("twitter:card", "summary_large_image", "name");

    // Data-driven schemas (Home FAQ, product specs, news article) are
    // installed by the page component once its async data arrives.
    const schema = staticSchema(name, route.path);
    if (schema !== null) setJsonLd(schema);
  }

  // Schemas that depend on data loaded asynchronously by the page component
  // (Home FAQ, product specs, news article) are cleared here and reinstalled
  // once that data arrives.
  function staticSchema(name: string, routePath: string): Record<string, unknown> | Record<string, unknown>[] | null {
    switch (name) {
      case "Home":
      case "ProductDetail":
      case "NewsDetail":
        return null;
      case "About":
        return [localBusiness(), breadcrumb(["Home", "About"], routePath)];
      default:
        return breadcrumb(routeCrumbs(name), routePath);
    }
  }
  function setMeta(name: string, content: string, attr = "name") {
    const sel = `${attr}="${name}"`;
    let el = document.querySelector<HTMLMetaElement>(`meta[${sel}]`);
    if (!el) {
      el = document.createElement("meta");
      el.setAttribute(attr, name);
      document.head.appendChild(el);
    }
    el.content = content;
  }

  return { updateMeta };
}
