# Security Policy

## Supported Versions

| Version | Supported          |
| ------- | ------------------ |
| 1.0.x   | :white_check_mark: |

## Privacy & Security Architecture Principles

BetShield is designed with a **privacy-first, local-only architecture**:

1. **Zero HTTPS Decryption:** BetShield does **NOT** install custom root certificates, does **NOT** inspect HTTPS payloads, and does **NOT** decrypt TLS connections.
2. **Local DNS Sinkhole:** Filtering occurs purely at the DNS lookup level via Android's local `VpnService` TUN interface without proxying traffic to external remote servers.
3. **No Surveillance:** The app does not collect, log, or exfiltrate passwords, credit cards, browsing history, messages, or keystrokes.
4. **Hashed Protection PIN:** PIN authentication uses SHA-256 with a unique randomized local salt stored in Android private encrypted preferences.
5. **No Exploits / Root:** The app operates strictly within standard Android platform boundaries.

## Reporting a Vulnerability

If you discover a security vulnerability within BetShield, please do not file a public issue. Instead, report it confidentially via GitHub Security Advisories or email the maintainers directly.
