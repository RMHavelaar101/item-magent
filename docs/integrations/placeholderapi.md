# PlaceholderAPI Integration

ItemMagnet registers a PlaceholderAPI expansion when PAPI is installed.

## Placeholders

| Placeholder | Value |
|-------------|-------|
| `%itemmagnet_active%` | `true` / `false` — player holds active magnet |
| `%itemmagnet_tier%` | Active tier id |
| `%itemmagnet_charge%` | Current charge |
| `%itemmagnet_max_charge%` | Max charge for active tier |
| `%itemmagnet_radius%` | Effective pull radius |
| `%itemmagnet_boost%` | Boost level |
| `%itemmagnet_boost_active%` | `true` / `false` — boost timer active |
| `%itemmagnet_filter_count%` | Personal filter rules (materials + tags) |
| `%itemmagnet_server_blacklist_count%` | Server blacklist rules (materials + tags) |

## Usage

```
/papi parse me %itemmagnet_tier%
/papi parse me %itemmagnet_filter_count%
```

Works in CMI holograms, scoreboards, tab list, and any PAPI-enabled plugin.

## Tested version

PlaceholderAPI **2.12.2** on Paper 1.21.x / 26.x.
