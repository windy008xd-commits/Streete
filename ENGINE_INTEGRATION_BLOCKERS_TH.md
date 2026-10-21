# StreetEmu v0.3 — สถานะการเชื่อมเอนจิน

## งานที่ทำเพิ่มในรอบนี้
- เพิ่มการตรวจสอบความเข้ากันได้พื้นฐานจาก Android จริง: API 30+ และ ABI `arm64-v8a`
- เพิ่มการอ่าน feature flags ของ Vulkan ที่ระบบ Android รายงาน
- ข้อความ UI ระบุชัดเจนว่าการตรวจ feature ไม่ใช่การยืนยันว่า GPU/ไดรเวอร์รองรับเอนจินทั้งหมด

## สิ่งที่ยังขาด จึงยังเปิดเกมไม่ได้
1. ซอร์สเอนจินจาก upstream `https://github.com/cherepavel/nfsu1-android` และ git submodules ที่ pin ไว้
2. `default.xbe` เวอร์ชัน Original Xbox NTSC-U ที่เอนจินรองรับ และไฟล์เกมที่ตรงกันจากสำเนาของผู้ใช้
3. Android SDK Platform 37, NDK `30.0.16248370`, CMake `4.1.2`, JDK 21+, Python 3 + venv/pip และ Gradle 9.8 ตาม upstream ปัจจุบัน
4. เชื่อม launcher กับ native activity/SDL/Vulkan, lifecycle, input, audio, save paths และนำการตั้งค่ากราฟิกไปควบคุม runtime จริง
5. Build และทดสอบบนอุปกรณ์ ARM64 จริง รวมถึงตรวจ crash, frame pacing และความร้อน

## ความพยายามดึงซอร์ส
การ `git clone` upstream ในสภาพแวดล้อมงานนี้ล้มเหลวเพราะไม่สามารถ resolve `github.com` ได้ (ไม่มีการเชื่อมต่อเครือข่ายจาก build container) จึงไม่สามารถรวมโค้ดเอนจินหรือยืนยัน build ได้ในรอบนี้

## Build status
- ยังไม่ได้ build APK: ไม่มี Android SDK/build-tools ในสภาพแวดล้อมนี้
- ไม่ใช่ playable APK และยังไม่ควรเผยแพร่ในฐานะแอปที่เล่นเกมได้
- ห้ามใส่ไฟล์เกมต้นฉบับลงในแพ็กเกจแอปหรือเผยแพร่โดยไม่มีสิทธิ์

## แนวทาง build เมื่อมีเครื่องมือครบ
ใช้คำสั่ง upstream จาก root ของ checkout: `./gradlew assembleDebug` โดยกำหนด `nfsu.xbe` ไปยังไฟล์เกมที่ถูกต้องตาม README ของ upstream จากนั้นตรวจ APK ที่ `android/app/build/outputs/apk/debug/app-debug.apk` และทดสอบจริงก่อนส่งมอบ
