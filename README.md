# Arkanoid: Global Offensive

**Arkanoid: Global Offensive** is a modern reinterpretation of the classic Arkanoid, reimagined with a real-time multiplayer system, server-authoritative architecture, and physics-based competitive gameplay.
This project was developed as part of the *Object-Oriented Programming* course at the **University of Engineering and Technology (UET), Vietnam National University**.

---

## 🧩 Overview

Arkanoid: Global Offensive builds upon the spirit of the original Arkanoid while introducing a new layer of depth and competitiveness. It combines classic brick-breaking mechanics with arena-style multiplayer battles and a modular, scalable architecture.<br><br>
Wonder how it plays? [View the Demo Youtube Video](https://youtu.be/zq4zS0_tc_A) (GitHub doesn't allow big large files, this video is 300MB)

---

## 🚀 Features

* **Server Authoritative Multiplayer (SAM)**
  Ensures fair and consistent online gameplay by handling all critical logic on the server side.

* **Entity-Component-System (ECS) Engine**
  Modular game architecture that separates data and behavior, improving maintainability and scalability.

* **Custom Network Protocol**
    PennyWort Protocol (TCP/IP), a minimalistic protocol designed from the ground up with consistent conventions and naming inspired by **Minecraft NMS 1.8.8**, built for real-time multiplayer synchronization while minimizing latency and network bandwidth.

* **Physics-Enabled Combat**
  A unique take on Arkanoid multiplayer featuring rifles, ball-to-ball collisions, and up to four players competing in an arena environment.

* **Classic Mode**
  Play the timeless single-player Arkanoid experience with modern visuals and controls.

* **Lobby and Room System**
  Fully featured multiplayer lobby allowing players to create, join, and manage game rooms within the same server instance.

* **And much much more!**
  It would take too long to describe all of it, so try it for yourself

---

## 🧰 Technologies and Libraries

* **JavaFX** — User interface framework for the desktop client
* **mjson** — Lightweight JSON library by *Borislav Iordanov*
* **Maven** — Build automation and dependency management tool

---

## ⚙️ Build and Run

### Prerequisites

* **Java 18** or higher
* **Maven** (3.8+ recommended)

### Build and Run the Game

```bash
mvn clean javafx:run
```

### Run the Tests

```bash
mvn test
```

## 🧑‍💻 Team
Omitted for privacy

© 2025 Arkanoi:GO Team. All rights reserved.