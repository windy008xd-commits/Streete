# แผนเชื่อมเอนจินเกม — StreetEmu

## เป้าหมาย
ทำให้แอปเป็น launcher สำหรับพอร์ตเกมจริง ไม่ใช่อีมูเลเตอร์ทั่วไป และไม่รวมไฟล์เกมใน APK

## งานตามลำดับ
1. **ยึดเอนจินเป้าหมายให้แน่นอน** — ใช้ nfsu1-android สำหรับ Need for Speed: Underground 1 เวอร์ชัน Original Xbox NTSC-U ที่รองรับเท่านั้น
2. **รวม source และ dependencies** — ดึง source/submodules จาก upstream, ตรวจ license และทำ reproducible build
3. **Game-data setup** — เลือกตำแหน่ง `default.xbe` และโฟลเดอร์เกม; ตรวจ edition และ layout ก่อนสร้าง generated code
4. **Native bridge** — เชื่อม Activity lifecycle, SDL window/surface, Vulkan renderer, audio, controller input และ touch overlay
5. **ตั้งค่าที่มีผลจริง** — map resolution scale, FPS cap และ input layout ไปยัง runtime; หาก runtime ไม่มี option ต้องแสดงว่า unsupported แทนการแสร้งว่าทำงาน
6. **Diagnostics** — เก็บ build ID, ABI, Android API, Vulkan capability, error log และเหตุผลที่เปิดเกมไม่ได้
7. **ทดสอบจริง** — build APK, ติดตั้ง, cold launch, resume/pause, โหลดสนาม, เล่นต่อเนื่อง และวัด FPS/thermal/memory บนอุปกรณ์ ARM64

## เกณฑ์รับงาน
- ไม่มีเกม data ใน APK
- โฟลเดอร์ผิดรุ่นต้องแสดง error ที่อ่านเข้าใจได้
- ปุ่ม Launch ต้องเปิดเกมจริงหรือแสดงสาเหตุที่เปิดไม่ได้อย่างชัดเจน
- ตัวเลือกกราฟิกต้องเปลี่ยน runtime จริงก่อนอ้างว่าเพิ่มประสิทธิภาพ
- ระบุอุปกรณ์ที่ทดสอบและผลวัด ไม่อ้าง FPS จากการคาดเดา

## สถานะปัจจุบัน
Launcher UI, folder picker, ตรวจไฟล์ระดับบนสุด และบันทึกค่าตั้งค่าเสร็จในระดับ source prototype แล้ว แต่ยังไม่ได้เชื่อม native runtime และยังไม่ได้ build/test APK ใน environment นี้
