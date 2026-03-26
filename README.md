# HIT-SHOT

> Simple, embeddable visitor counter badge for your website.

---

## What is HIT-SHOT?

HIT-SHOT generates an SVG badge that counts visitors to your site. Drop a single `<img>` tag into your page and you're
done — no JavaScript SDK, no account signup, no tracking scripts.

**Today / Total** counts are displayed in a clean badge like this:

```
┌──────────┬──────────┐
│ hit-shot │  3 / 128 │
└──────────┴──────────┘
```

## Features

- **Zero-config badge** — just add an image tag
- **Today / Total** count in a single badge
- **Daily statistics** with chart visualization
- **Custom badge color** via `color` parameter
- **Rate limiting** — 500 req/min per domain (Bucket4j)
- **No cookies, no JS, no fingerprinting**

## Quick Start

### HTML

```html
<a href="https://hit.kmapshot.com">
    <img src="https://hit.kmapshot.com/api/badge/svg?url=https://your-site.com" alt="hit-shot">
</a>
```

### Markdown

```markdown
[![hit-shot](https://hit.kmapshot.com/api/badge/svg?url=https://your-site.com)](https://hit.kmapshot.com)
```

### BBCode

```
[url=https://hit.kmapshot.com][img]https://hit.kmapshot.com/api/badge/svg?url=https://your-site.com[/img][/url]
```

## API

| Endpoint                                            | Method | Description                                   |
|-----------------------------------------------------|--------|-----------------------------------------------|
| `/api/badge/svg?url={url}&color={hex}`              | GET    | Increment count and return SVG badge          |
| `/api/badge/hidden?url={url}`                       | GET    | Increment count without badge response        |
| `/api/badge/view/total-count?url={url}&color={hex}` | GET    | View-only badge (no increment)                |
| `/api/badge/stats/daily?url={url}&days={n}`         | GET    | Daily visitor stats as JSON (default 30 days) |

### Parameters

| Parameter | Required | Default | Description                    |
|-----------|----------|---------|--------------------------------|
| `url`     | Yes      | —       | Target site URL                |
| `color`   | No       | `#4c1`  | Badge color (hex)              |
| `days`    | No       | `30`    | Number of days for daily stats |

### Example: Daily Stats Response

```json
{
  "url": "your-site.com",
  "today": 12,
  "total": 1583,
  "daily": {
    "02-25": 41,
    "02-26": 38,
    "02-27": 55
  }
}
```

## Tech Stack

| Layer         | Technology      |
|---------------|-----------------|
| Framework     | Spring Boot 3.4 |
| Language      | Java 17         |
| Database      | MongoDB Atlas   |
| Rate Limiting | Bucket4j        |
| Template      | Thymeleaf       |
| Build         | Gradle          |

## Architecture

```
presentation/
├── api/        BadgeController (REST API)
└── view/       HomeController (Web UI)

application/
├── VisitCounter    Orchestrates count logic
├── UrlParser       URL validation & normalization
└── LogoMaker       SVG badge generation

domain/
├── sites/          Site entity & visitor count
├── dailyScore/     Daily visit tracking
└── buckets/        Rate limiting per domain
```

## Running Locally

```bash
# 1. Set up application-local.properties
cp src/main/resources/application.properties src/main/resources/application-local.properties
# Edit application-local.properties with your MongoDB URI:
# spring.data.mongodb.uri=mongodb+srv://...

# 2. Run with local profile
./gradlew bootRun --args='--spring.profiles.active=local'
```

Open [http://localhost:8080](http://localhost:8080) and generate your first badge.

## License

MIT
