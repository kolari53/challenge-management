📱 Challenge Management API — Case Study

 🔍 Genel Bakış
 
 Challenge Management API, taşınabilirlik (port-out) sürecinde, müşteriye ait hattın doğrulama
 (challenge) akışını yöneten bir Spring Boot 3.5.7 tabanlı servistir.
 
 Bu servis, Portability Management API’den gelen port-out taleplerini alır, hattın uygunluğunu BSS
 sisteminde doğrular, müşteri onayı için SMS bildirimi gönderir, süreci takip eder ve sonuçlarını hem
 müşteriye hem de Portability sistemine iletir.
 
 ⚙️ Teknoloji Stack
 
     Katman Teknoloji
     Backend Java 25, Spring Boot 3.5.7
     Build Tool Maven
     ORM Spring Data JPA (Hibernate)
     Veritabanı PostgreSQL
     Loglama SLF4J / Lombok @Slf4j
     API Dokümantasyonu Swagger / OpenAPI
     Test JUnit 5, Mockito
     Scheduler Spring @Scheduled
     Patternler State, Strategy, Service Layer, Repository, Audit Log Pattern
 
🧠 Sistem Bileşenleri
 
   1. ChallengeService
   
     Tüm iş akışının merkezidir.
   
     Sorumlulukları:
     
         MSISDN doğrulama (BSS mock servisi ile)
         Eşzamanlı challenge kontrolü
         Challenge oluşturma (createChallenge)
         Müşteri yanıtı (reply)
         İptal (cancel)
         Süre aşımı (expireChallenges)
   
   💡 Challenge durumu Enum olarak yönetilir:
  
      ACKNOWLEDGED → PENDING → ACCEPTED / REJECTED / CANCELLED / EXPIRED
 
   2. BSSValidationService
    
           Mock bir doğrulama servisi olup hattın:
            kayıtlı (isRegistered )
             aktif (isActive )
             olup olmadığını kontrol eder.
   
   3. NotificationService

    Bildirim gönderimlerinden sorumludur.
   
    Strategy Pattern ile hem SMS hem Email stratejilerini destekler:
   
         SMSNotificationStrategy
         EmailNotificationStrategy
   
    SMS bildirimi zorunlu, e-posta bildirimi opsiyonel olarak uygulanmıştır.
 
   4.AuditService & ChallengeHistory
   
     Tüm olayları 
   
      ChallengeHistoryLog tablosuna yazar.
    
     Kısaca kayıtlar:
   
         Challenge oluşturma, onay, reddetme, iptal, süre aşımı gibi state değişimleri
         CSR kullanıcı işlemleri
         Müşteri yanıtları
   
   5. CSRController
   
     Yetkili kullanıcıların manuel işlem yapabilmesini sağlar:
     
         /api/csr/accept
         /api/csr/reject
     
     Audit kayıtları ile birlikte CSR kimliği loglanır.
  
   6. PortabilityClient
  
     Challenge sonucunu Portability Management sistemine bildiren mock bir adapter.
     
     Sadece log üzerinden simülasyon yapılır:
     
         notifyChallengeAccepted
         notifyChallengeRejected
         notifyChallengeCancelled
         notifyChallengeExpired
     
   7.ChallengeExpirationJob (Scheduler)
   
     Zaman aşımına uğramış (expired) challenge’ları periyodik olarak kontrol eder.
     
     @Scheduled(fixedRate = 300000) (her 5 dakikada bir) çalışır.
     
     Süre dolan challenge’ların durumu “EXPIRED” olarak güncellenir ve audit kaydı oluşturulur.
   
 🗺 Acceptance Criteria Karşılığı
 Case Maddesi
 Durum Açıklama
 1. Challenge oluşturma ve doğrulama ✅
 BSS doğrulaması ve eşzamanlı kontrol yapılıyor
 2. SMS bildirimi ✅
 NotificationService ile gönderiliyor
 2. Email bildirimi ⚙️
 EmailStrategy mevcut (opsiyonel)
 3. Müşteri yanıtı (Yes/No) ✅
 reply() metodu ile yönetiliyor
 3. Portability bildirimi ✅
 PortabilityClient ile simüle ediliyor
 4. CSR müdahalesi ✅
 CSRController ile sağlanıyor
 5. Challenge iptali ✅
 cancel() + audit + notification
 6. Süre aşımı ✅
 Scheduler ile expire ediliyor
 7. Loglama ve izlenebilirlik ✅
 ChallengeHistory tablosu ile kayıt altına alınıyor

 🧪 Test Yapısı
 
 Unit Testler
 
    ChallengeServiceTest → tüm business metodları için mock testler.
     Mockito ile repository ve service mock’ları oluşturulmuştur.
     Integration Test (Opsiyonel)
 
    ChallengeFlowIntegrationTest → uçtan uca create → expire akışını doğrular.

� Swagger Dokümantasyonu

 Swagger otomatik olarak aktif:
 http://localhost:8080/swagger-ui/index.html
 Buradan tüm uçlar test edilebilir:

 /api/challenge/create
 /api/challenge/reply
 /api/challenge/cancel
 /api/csr/accept
 /api/csr/reject
 
 🗄Veritabanı Şeması
 
 Table: challenge | id | msisdn | account | language | status | expires_at |
 Table: challenge_history | id | challenge_id | action | actor | description | timestamp |
 
 🚀 Örnek Akış
 
 1. Create Challenge

 POST /api/challenge/create?msisdn=5301111112&account=KOLARI&lang=tr
 → status: ACKNOWLEDGED → PENDING
 → SMS bildirimi gönderilir.
 3. Customer Reply
 
 POST /api/challenge/reply?msisdn=5301111112&reply=YES
 → status: ACCEPTED
 → Audit log + Portability bildirimi yapılır.
 
 4. Expire Job → Süre dolan challenge’lar EXPIRED olur.
 
 → Audit log + PortabilityClient bildirimi.
 
🧾 Test Dataset (Örnek MSISDN Küme Seti)

 Senaryo MSISDN BSS
 Kaydı Aktiflik Beklenen Durum
 
 🟢 Geçerli (kayıtlı + aktif) 5301111112 ✅ ✅ Challenge oluşturulur
 
 🟢 Geçerli (kayıtlı + aktif) 5398765434 ✅ ✅ Challenge oluşturulur
 
 🔴 Kayıtlı ama inaktif 5301111111 ✅ ❌ “MSISDN is not active”
 hatası
 
 🔴 Geçersiz (BSS dışı) 5011111112 ❌ ❌ “MSISDN not registered in
 BSS” hatası
 
 🟠 Aynı hatta ikinci
 challenge 5301111112 ✅ ✅ “Active challenge already
 exists” hatası
 
 🟢 Challenge expirasyon
 testine uygun 5309999998 ✅ ✅ 5 dk sonra EXPIRED olmalı
 
 🔍 Test Önerileri
 Yeni Challenge oluşturma:
 POST /api/challenge/create?msisdn=5301111112&account=KOLARI&lang=tr
 
 Yanıt gönderme (YES):
 POST /api/challenge/reply?msisdn=5301111112&reply=YES
 
 Yanıt gönderme (NO):
 POST /api/challenge/reply?msisdn=5398765434&reply=NO
 
 Süre aşımı testi:
 5309999998 için challenge oluştur.
 
 5 dakika bekle veya manuel olarak expiration job’u çağır.
 Durumu “EXPIRED” olmalı.
