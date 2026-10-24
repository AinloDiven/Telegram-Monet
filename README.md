# Telegram Monet Radical

<p align="center">
  <b>适用于官方 Telegram 最新版的高级 Material 3 Expressive 主题生成器与 Pixel 动态几何壁纸引擎</b><br>
  <i>Tailored exclusively for Modern Telegram (Android 8.0+ / Android 14 / 15 / 16 / 17)</i>
</p>

<p align="center">
  <a href="https://github.com/AinloDiven/Telegram-Monet/releases"><img src="https://img.shields.io/github/v/release/AinloDiven/Telegram-Monet?style=flat-square&color=4285F4" alt="Release"></a>
  <a href="https://github.com/AinloDiven/Telegram-Monet/blob/main/LICENSE"><img src="https://img.shields.io/github/license/AinloDiven/Telegram-Monet?style=flat-square" alt="License"></a>
  <a href="https://t.me/tgmonet"><img src="https://img.shields.io/badge/Telegram-Channel-2CA5E0?style=flat-square&logo=telegram" alt="Telegram"></a>
</p>

---

## 🌟 核心特色 (Core Highlights)

### 1. 纯净为现代 Telegram 而生 (Modern Telegram Only)
- **彻底去除历史包袱**：完全剔除已过时的 Telegram X 兼容层与冗余伪键（如 `chat_blur_*`、`actionBarDefaultSub` 等），严格遵循官方 Telegram 最新版主题架构。
- **彻底修复 Android 15 / 17 输入框文字变透明 Bug**：
  - 针对 Android 15 (Vanilla Ice Cream) 及 Android 17 系统 HWUI 硬件加速合成器机制进行底层适配。
  - 原生 Telegram 底部输入框（`messageEditText`）在重绘时依赖 `saveLayerAlpha` 与 `DST_OUT` 模式；若主题为 `chat_messagePanelBackground` 配置了非标准透明通道，切到后台恢复时会导致 RenderNode 脏缓存使文字字形 Alpha 通道被归零。
  - 全面遵循 Telegram 官方最新规范，将输入面板、顶栏等容器保持规范的基底色彩，交由 Telegram 原生 RenderNode 高斯模糊引擎动态处理，彻底根除切出后台切回时输入框文字隐形的恶性问题。
- **规范文本选择高亮通道**：修正 `chat_textSelectBackground` 与 `chat_outTextSelectionHighlight` 的半透明度通道（`~35%` / `~50%`），杜绝不透明遮挡问题。

### 2. 半透明聊天气泡 (Translucent Chat Bubbles)
- **透光微透质感**：收发消息气泡具备精调的 80% ~ 85% 半透明度，光线柔和穿透，与底层壁纸自然交融。
- **清除黑重阴影**：剔除气泡下方沉重死板的黑色硬阴影（`chat_inBubbleShadow=0`），展现微透浮空美感。
- **独立控制开关**：在设置中心支持一键开启或关闭半透明气泡模式。

### 3. Pixel 几何与 M3 Expressive 壁纸引擎 (Wallpaper Engine)
遵循 [Google Material 3 Expressive 规范](https://m3.material.io/blog/building-with-m3-expressive) 与 Pixel 官方壁纸几何学，提供矢量级别的动态几何壁纸生成：
- **官方全套 M3 Expressive 形状库**：
  - 🌊 **海胆 / 放射爆炸星芒 (Sea Urchin / Soft Burst)**：12 角圆滑尖峰与平滑凹谷的标志性海胆。
  - 🍀 **四叶草 / 八叶草 (Clover)**：柔美花瓣轮廓的极简四叶草与八叶草。
  - ☀️ **阳光太阳 (Sunny / Sunburst)**：饱满多射线暖阳轮廓。
  - 🌸 **贝壳花朵 (Pixel Bloom)**：Pixel Android 12-15 锁屏时钟招牌的 12 瓣贝壳花朵徽章。
  - 🧊 **45° 膨胀菱形 (Diamond Squircle)**：官方极简悬浮几何方块。
  - 🏜️ **丝滑沙丘 (Silk Dunes)**：多色阶斜向流动沙丘层叠带。
- **真多布局换构图 (Genuine Multi-Layout Engine)**：
  - 点击「🎲 换个构图」直接在**官方标准位、水平镜像位、对角张力位、纵轴脊柱位**之间重组骨架，并伴随主副色阶轮转。
- **大面积呼吸留白**：中央区域整洁干净，避免密集杂乱的小散点，完美衬托聊天内容。

### 4. Pixel 几何壁纸一键生成与保存 (Pixel Wallpaper Generator)
- 结合当前 Monet 提取的调色板实时生成 1200×2670 超高清矢量几何壁纸。
- 支持一键保存至系统相册（`Pictures/TgMonet`），在 Telegram「设置 > 聊天设置 > 更换聊天背景」中选中即可应用。
- 遵循 Telegram 最新规范：官方 `.attheme` 标准仅处理颜色与云端 Slug，不使用且不依赖已失效的旧版 WPS 字节流注入，确保主题格式纯净轻巧与稳定。

---

## 🎨 壁纸预设 (Wallpaper Presets)

| 预设名称 | 核心元素 | 视觉特征 |
| :--- | :--- | :--- |
| **M3 表现力 (M3 Expressive)** | 海胆 (Sea Urchin)、四叶草 (Clover)、阳光 (Sunny) | 充满活力、官方 M3 Expressive 核心图集 |
| **贝壳花朵 (Pixel Bloom)** | 12瓣贝壳花徽章、穹顶拱形、侧边纵向波浪 | 纯正 Google Pixel 官方锁屏时钟美学 |
| **悬浮极简 (Floating Geometry)**| 45° 倒角菱形、有机软云 (Amoeba) | 大面积开阔留白、呼吸感极强 |
| **丝滑沙丘 (Silk Dunes)** | 连续斜向贝塞尔流动层叠带、角隅点缀 | 平滑丝滑、流动多色阶沙丘 |
| **经典几何 (Pixel Classic)** | 紧凑微缩波浪、中心球体、倾斜胶囊方块 | 经典 Pixel 几何微缩精修版 |
| **包豪斯现代 (Bauhaus Modern)** | 建筑感立柱、重叠圆环、极简几何基底 | 理性现代主义构图 |

---

## 📥 下载与安装 (Download)

请前往项目 [GitHub Releases](https://github.com/AinloDiven/Telegram-Monet/releases) 下载最新 APK：
- 最新版本：`TGMonet-Radical-v14.5.0.apk`
- 支持直接覆盖更新已安装的旧版 TGMonet Radical。

---

## 🛠️ 构建与开发 (Build)

```bash
# 克隆仓库
git clone https://github.com/AinloDiven/Telegram-Monet.git
cd Telegram-Monet

# 构建 Debug APK
./gradlew assembleDebug

# 构建 Release APK
./gradlew assembleRelease
```

---

## 📄 开源许可 (License)
本项目基于 [MIT License](LICENSE) 开源。
Material 3 Design & Color Utilities Copyright (c) Google LLC.
