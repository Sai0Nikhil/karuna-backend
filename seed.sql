-- ============================================================
-- Karuṇā — Seed data for development / demonstration
-- Password for all users: password123
-- ============================================================

-- ─── Users ─────────────────────────────────────────────────────

INSERT INTO app_user (email, password, name, phone, role, ngo_name, available)
VALUES
  ('ngo@karuna.app',  '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Vijayawada Animal Trust',   '91-866-1234567', 'NGO',       'Vijayawada Animal Trust',  true),
  ('volunteer@k.com',  '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Ravi Kumar',              '91-9876543210', 'VOLUNTEER', null,                       true),
  ('sunita@k.com',     '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Sunita Patel',            '91-9876543211', 'VOLUNTEER', null,                       true),
  ('arjun@k.com',      '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Arjun Reddy',             '91-9876543212', 'VOLUNTEER', null,                       true),
  ('lakshmi@k.com',    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Lakshmi Devi',            '91-9876543213', 'VOLUNTEER', null,                       true),
  ('citizen@k.com',    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Priya Sharma',            '91-9876543214', 'CITIZEN',   null,                       null),
  ('anand@k.com',      '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Anand Rao',              '91-9876543215', 'CITIZEN',   null,                       null);

-- ─── Cases ─────────────────────────────────────────────────────

INSERT INTO animal_case (reporter_id, responder_id, image_data_url, location_label, latitude, longitude, species, injury_type, severity, status, probable_condition, first_aid_steps, estimated_cost_inr, notes, created_at, updated_at)
VALUES
  -- Case 1: Fully resolved — stray dog hit by auto, treated & adopted
  (6, 2,
   'data:image/jpeg;base64,/9j/4AAQSkZJRgABAQAAAQABAAD/2wBDAAgGBgcGBQgHBwcJCQgKDBQNDAsLDBkSEw8UHRofHh0aHBwgJC4nICIsIxwcKDcpLDAxNDQ0Hyc5PTgyPC4zNDL/2wBDAQkJCQwLDBgNDRgyIRwhMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjL/wAARCAABAAEDASIAAhEBAxEB/8QAHwAAAQUBAQEBAQEAAAAAAAAAAAECAwQFBgcICQoL/8QAtRAAAgEDAwIEAwUFBAQAAAF9AQIDAAQRBRIhMUEGE1FhByJxFDKBkaEII0KxwRVS0fAkM2JyggkKFhcYGRolJicoKSo0NTY3ODk6Q0RFRkdISUpTVFVWV1hZWmNkZWZnaGlqc3R1dnd4eXqDhIWGh4iJipKTlJWWl5iZmqKjpKWmp6ipqrKztLW2t7i5usLDxMXGx8jJytLT1NXW19jZ2uHi4+Tl5ufo6erx8vP09fb3+Pn6/8QAHwEAAwEBAQEBAQEBAQAAAAAAAAECAwQFBgcICQoL/8QAtREAAgECBAQDBAcFBAQAAQJ3AAECAxEEBSExBhJBUQdhcRMiMoEIFEKRobHBCSMzUvAVYnLRChYkNOEl8RcYI4Q/SFhSRFJiMkVic4EzQjR0RSlFNkVUcCZS/9oADAMBAAIRAxEAPwC1q+l6bfeI/Ekmo3NxHNBehII4ckSyeWpVSB1BOOmeacNG0qbwPoeqww3tpc3MjwSSPM5WNlMgXA6jhD+dR654isYvGHiawnSXz7mG3Ns6qCofYQd+e3I6Z6VHb+JLCLwDpmjTpc/bob0XJZEzGFBc45zyN/oa8x3srev4HaqLsntpZW8rv8AE3bvTbfS/C3hW9sXuGmnv44pXmcspYMwztPTgetbGsWMN/4ttdPnaczXVjOjPG5UoF2kHj1569a5G/8AElhN4M0XS4obkXGn3q3EpaMbSgLHK89fmWt7W/FGn6b4p0/VYrW+mkjspIXRogBuyCF+/wCmfyrWNveT7f5ozq0JO0IpttR7/v5J6b2dvuMvTbuyg0jwV9pjvDK0bjNvMqBPmz82fvd+n5iui8G2FtYXut2lqJDbwXKoiddo8tT0/nXJ6D4n0jS7TwqLqG7e4sYXWTy4gQC2cEHcM9q3PAniDS7bUNdtriSVZrm8VrfYhYOVVQBuHTr3x0Na0mrJP+tDDG0ajqTSTseT3v/AB/3X/XaT/0I1FVm7/wCP+6/67Sf+hGqlYy3Z0U/hR//2Q==',
   'Labbipet, Vijayawada', 16.5062, 80.6480, 'dog', 'leg_injury', 'urgent',
   'discharged',
   'Adult male stray dog hit by auto-rickshaw, deep laceration on right hind leg, bleeding heavily',
   '["Approach slowly and speak softly to calm the animal.","Apply gentle pressure around the wound with clean cloth to control bleeding.","If bleeding stops, clean the wound with saline or clean water. Do NOT pour hydrogen peroxide into deep wounds.","Cover with sterile gauze and bandage loosely.","Transport to nearest veterinary clinic immediately — the leg may need suturing."]',
   3500,
   '["Arrived at site within 15 min of report. Dog was frightened but not aggressive.","Applied first aid and transported to VetCare clinic on MG Road.","Vet examined — 8cm laceration, no fracture. Required 12 stitches.","3-day observation: wound healing well, no signs of infection.","Day 7: Stitches removed. Dog recovering well at shelter.","Day 14: Fully recovered and vaccinated.","Adopted by local family — renamed Bhairava."]',
   '2026-01-15 08:30:00', '2026-02-05 14:00:00'),

  -- Case 2: Still in treatment — dog with mange
  (7, 3,
   'data:image/jpeg;base64,/9j/4AAQSkZJRgABAQAAAQABAAD/2wBDAAgGBgcGBQgHBwcJCQgKDBQNDAsLDBkSEw8UHRofHh0aHBwgJC4nICIsIxwcKDcpLDAxNDQ0Hyc5PTgyPC4zNDL/2wBDAQkJCQwLDBgNDRgyIRwhMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjL/wAARCAABAAEDASIAAhEBAxEB/8QAHwAAAQUBAQEBAQEAAAAAAAAAAAECAwQFBgcICQoL/8QAtRAAAgEDAwIEAwUFBAQAAAF9AQIDAAQRBRIhMUEGE1FhByJxFDKBkaEII0KxwRVS0fAkM2JyggkKFhcYGRolJicoKSo0NTY3ODk6Q0RFRkdISUpTVFVWV1hZWmNkZWZnaGlqc3R1dnd4eXqDhIWGh4iJipKTlJWWl5iZmqKjpKWmp6ipqrKztLW2t7i5usLDxMXGx8jJytLT1NXW19jZ2uHi4+Tl5ufo6erx8vP09fb3+Pn6/8QAHwEAAwEBAQEBAQEBAQAAAAAAAAECAwQFBgcICQoL/8QAtREAAgECBAQDBAcFBAQAAQJ3AAECAxEEBSExBhJBUQdhcRMiMoEIFEKRobHBCSMzUvAVYnLRChYkNOEl8RcYI4Q/SFhSRFJiMkVic4EzQjR0RSlFNkVUcC/9oADAMBAAIRAxEAPwC1q+l6bfeI/Ekmo3NxHNBehII4ckSyeWpVSB1BOOmeacNG0qbwPoeqww3tpc3MjwSSPM5WNlMgXA6jhD+dR654isYvGHiawnSXz7mG3Ns6qCofYQd+e3I6Z6VHb+JLCLwDpmjTpc/bob0XJZEzGFBc45zyN/oa8x3srev4HaqLsntpZW8rv8AE3bvTbfS/C3hW9sXuGmnv44pXmcspYMwztPTgetbGsWMN/4ttdPnaczXVjOjPG5UoF2kHj1569a5G/8AElhN4M0XS4obkXGn3q3EpaMbSgLHK89fmWt7W/FGn6b4p0/VYrW+mkjspIXRogBuyCF+/wCmfyrWNveT7f5ozq0JO0IpttR7/v5J6b2dvuMvTbuyg0jwV9pjvDK0bjNvMqBPmz82fvd+n5iui8G2FtYXut2lqJDbwXKoiddo8tT0/nXJ6D4n0jS7TwqLqG7e4sYXWTy4gQC2cEHcM9q3PAniDS7bUNdtriSVZrm8VrfYhYOVVQBuHTr3x0Na0mrJP+tDDG0ajqTSTseT3v/AB/3X/XaT/0I1FVm7/wCP+6/67Sf+hGqlYy3Z0U/hR//2Q==',
   'Benz Circle, Vijayawada', 16.5086, 80.6440, 'dog', 'skin_disease', 'routine',
   'in_treatment',
   'Female stray dog with severe sarcoptic mange — extensive hair loss, crusty lesions on back and ears',
   '["Wear gloves before approaching — mange can be transmitted to humans (scabies).","Isolate from other animals if possible.","Apply coconut oil or olive oil to soften crusts — do NOT pick at scabs.","Prepare medicated bath: mix 1 tbsp sulfur powder with 1 liter warm water. Bathe the animal gently.","Repeat bath every 3 days for 2 weeks.","Take to vet for ivermectin injection — this is the most effective treatment."]',
   2000,
   '["Spotted by citizen near Benz Circle traffic island. Animal appeared weak but approachable.","Transported to shelter. Vet confirmed severe sarcoptic mange. Started ivermectin course.","First sulfur bath given at shelter. Animal eating well.","Week 2: noticeable improvement — new hair growth on back.","Week 3: crusts mostly healed. Continuing treatment."]',
   '2026-02-10 10:15:00', '2026-03-05 16:30:00'),

  -- Case 3: Reported, needs dispatch — cat stuck in drain
  (6, null,
   'data:image/jpeg;base64,/9j/4AAQSkZJRgABAQAAAQABAAD/2wBDAAgGBgcGBQgHBwcJCQgKDBQNDAsLDBkSEw8UHRofHh0aHBwgJC4nICIsIxwcKDcpLDAxNDQ0Hyc5PTgyPC4zNDL/2wBDAQkJCQwLDBgNDRgyIRwhMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjL/wAARCAABAAEDASIAAhEBAxEB/8QAHwAAAQUBAQEBAQEAAAAAAAAAAAECAwQFBgcICQoL/8QAtRAAAgEDAwIEAwUFBAQAAAF9AQIDAAQRBRIhMUEGE1FhByJxFDKBkaEII0KxwRVS0fAkM2JyggkKFhcYGRolJicoKSo0NTY3ODk6Q0RFRkdISUpTVFVWV1hZWmNkZWZnaGlqc3R1dnd4eXqDhIWGh4iJipKTlJWWl5iZmqKjpKWmp6ipqrKztLW2t7i5usLDxMXGx8jJytLT1NXW19jZ2uHi4+Tl5ufo6erx8vP09fb3+Pn6/8QAHwEAAwEBAQEBAQEBAQAAAAAAAAECAwQFBgcICQoL/8QAtREAAgECBAQDBAcFBAQAAQJ3AAECAxEEBSExBhJBUQdhcRMiMoEIFEKRobHBCSMzUvAVYnLRChYkNOEl8RcYI4Q/SFhSRFJiMkVic4EzQjR0RSlFNkVUcC/9oADAMBAAIRAxEAPwC1q+l6bfeI/Ekmo3NxHNBehII4ckSyeWpVSB1BOOmeacNG0qbwPoeqww3tpc3MjwSSPM5WNlMgXA6jhD+dR654isYvGHiawnSXz7mG3Ns6qCofYQd+e3I6Z6VHb+JLCLwDpmjTpc/bob0XJZEzGFBc45zyN/oa8x3srev4HaqLsntpZW8rv8AE3bvTbfS/C3hW9sXuGmnv44pXmcspYMwztPTgetbGsWMN/4ttdPnaczXVjOjPG5UoF2kHj1569a5G/8AElhN4M0XS4obkXGn3q3EpaMbSgLHK89fmWt7W/FGn6b4p0/VYrW+mkjspIXRogBuyCF+/wCmfyrWNveT7f5ozq0JO0IpttR7/v5J6b2dvuMvTbuyg0jwV9pjvDK0bjNvMqBPmz82fvd+n5iui8G2FtYXut2lqJDbwXKoiddo8tT0/nXJ6D4n0jS7TwqLqG7e4sYXWTy4gQC2cEHcM9q3PAniDS7bUNdtriSVZrm8VrfYhYOVVQBuHTr3x0Na0mrJP+tDDG0ajqTSTseT3v/AB/3X/XaT/0I1FVm7/wCP+6/67Sf+hGqlYy3Z0U/hR//2Q==',
   'Patamata, Vijayawada', 16.5110, 80.6600, 'cat', 'trapped', 'routine',
   'reported',
   'Kitten stuck in drainage pipe near Patamata market — crying continuously, unable to get out',
   '["Do NOT try to pull the animal out by force — this can cause spinal injury.","Try luring with food (tuna or wet cat food) placed at the pipe entrance.","If the pipe is long, locate the other end and see if the animal can crawl toward you.","Use a soft cloth to gently guide the animal if it is close to an opening.","Call for professional rescue if the animal is more than 3 feet inside or seems stuck."]',
   800,
   '[]',
   '2026-03-01 14:20:00', '2026-03-01 14:20:00'),

  -- Case 4: Reported, unassigned — cow with wound
  (7, null,
   'data:image/jpeg;base64,/9j/4AAQSkZJRgABAQAAAQABAAD/2wBDAAgGBgcGBQgHBwcJCQgKDBQNDAsLDBkSEw8UHRofHh0aHBwgJC4nICIsIxwcKDcpLDAxNDQ0Hyc5PTgyPC4zNDL/2wBDAQkJCQwLDBgNDRgyIRwhMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjL/wAARCAABAAEDASIAAhEBAxEB/8QAHwAAAQUBAQEBAQEAAAAAAAAAAAECAwQFBgcICQoL/8QAtRAAAgEDAwIEAwUFBAQAAAF9AQIDAAQRBRIhMUEGE1FhByJxFDKBkaEII0KxwRVS0fAkM2JyggkKFhcYGRolJicoKSo0NTY3ODk6Q0RFRkdISUpTVFVWV1hZWmNkZWZnaGlqc3R1dnd4eXqDhIWGh4iJipKTlJWWl5iZmqKjpKWmp6ipqrKztLW2t7i5usLDxMXGx8jJytLT1NXW19jZ2uHi4+Tl5ufo6erx8vP09fb3+Pn6/8QAHwEAAwEBAQEBAQEBAQAAAAAAAAECAwQFBgcICQoL/8QAtREAAgECBAQDBAcFBAQAAQJ3AAECAxEEBSExBhJBUQdhcRMiMoEIFEKRobHBCSMzUvAVYnLRChYkNOEl8RcYI4Q/SFhSRFJiMkVic4EzQjR0RSlFNkVUcC/9oADAMBAAIRAxEAPwC1q+l6bfeI/Ekmo3NxHNBehII4ckSyeWpVSB1BOOmeacNG0qbwPoeqww3tpc3MjwSSPM5WNlMgXA6jhD+dR654isYvGHiawnSXz7mG3Ns6qCofYQd+e3I6Z6VHb+JLCLwDpmjTpc/bob0XJZEzGFBc45zyN/oa8x3srev4HaqLsntpZW8rv8AE3bvTbfS/C3hW9sXuGmnv44pXmcspYMwztPTgetbGsWMN/4ttdPnaczXVjOjPG5UoF2kHj1569a5G/8AElhN4M0XS4obkXGn3q3EpaMbSgLHK89fmWt7W/FGn6b4p0/VYrW+mkjspIXRogBuyCF+/wCmfyrWNveT7f5ozq0JO0IpttR7/v5J6b2dvuMvTbuyg0jwV9pjvDK0bjNvMqBPmz82fvd+n5iui8G2FtYXut2lqJDbwXKoiddo8tT0/nXJ6D4n0jS7TwqLqG7e4sYXWTy4gQC2cEHcM9q3PAniDS7bUNdtriSVZrm8VrfYhYOVVQBuHTr3x0Na0mrJP+tDDG0ajqTSTseT3v/AB/3X/XaT/0I1FVm7/wCP+6/67Sf+hGqlYy3Z0U/hR//2Q==',
   'Autonagar, Vijayawada', 16.5190, 80.6740, 'cow', 'wound', 'critical',
   'reported',
   'Adult cow with deep gash on neck — possibly from barbed wire. Bleeding profusely, animal agitated.',
   '["Approach from the side — cows have blind spots directly in front and behind.","Do NOT make sudden movements. Speak in a low, calm voice.","If bleeding heavily, pack the wound with clean cloth and apply firm pressure.","Lead the animal to a safe enclosure if possible.","Call large animal veterinarian URGENTLY — this wound needs suturing and tetanus shot.","Keep other animals and people away to reduce stress on the cow."]',
   5000,
   '[]',
   '2026-03-05 07:45:00', '2026-03-05 07:45:00'),

  -- Case 5: Rescue route — dog hit by vehicle, being transported
  (6, 4,
   'data:image/jpeg;base64,/9j/4AAQSkZJRgABAQAAAQABAAD/2wBDAAgGBgcGBQgHBwcJCQgKDBQNDAsLDBkSEw8UHRofHh0aHBwgJC4nICIsIxwcKDcpLDAxNDQ0Hyc5PTgyPC4zNDL/2wBDAQkJCQwLDBgNDRgyIRwhMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjL/wAARCAABAAEDASIAAhEBAxEB/8QAHwAAAQUBAQEBAQEAAAAAAAAAAAECAwQFBgcICQoL/8QAtRAAAgEDAwIEAwUFBAQAAAF9AQIDAAQRBRIhMUEGE1FhByJxFDKBkaEII0KxwRVS0fAkM2JyggkKFhcYGRolJicoKSo0NTY3ODk6Q0RFRkdISUpTVFVWV1hZWmNkZWZnaGlqc3R1dnd4eXqDhIWGh4iJipKTlJWWl5iZmqKjpKWmp6ipqrKztLW2t7i5usLDxMXGx8jJytLT1NXW19jZ2uHi4+Tl5ufo6erx8vP09fb3+Pn6/8QAHwEAAwEBAQEBAQEBAQAAAAAAAAECAwQFBgcICQoL/8QAtREAAgECBAQDBAcFBAQAAQJ3AAECAxEEBSExBhJBUQdhcRMiMoEIFEKRobHBCSMzUvAVYnLRChYkNOEl8RcYI4Q/SFhSRFJiMkVic4EzQjR0RSlFNkVUcC/9oADAMBAAIRAxEAPwC1q+l6bfeI/Ekmo3NxHNBehII4ckSyeWpVSB1BOOmeacNG0qbwPoeqww3tpc3MjwSSPM5WNlMgXA6jhD+dR654isYvGHiawnSXz7mG3Ns6qCofYQd+e3I6Z6VHb+JLCLwDpmjTpc/bob0XJZEzGFBc45zyN/oa8x3srev4HaqLsntpZW8rv8AE3bvTbfS/C3hW9sXuGmnv44pXmcspYMwztPTgetbGsWMN/4ttdPnaczXVjOjPG5UoF2kHj1569a5G/8AElhN4M0XS4obkXGn3q3EpaMbSgLHK89fmWt7W/FGn6b4p0/VYrW+mkjspIXRogBuyCF+/wCmfyrWNveT7f5ozq0JO0IpttR7/v5J6b2dvuMvTbuyg0jwV9pjvDK0bjNvMqBPmz82fvd+n5iui8G2FtYXut2lqJDbwXKoiddo8tT0/nXJ6D4n0jS7TwqLqG7e4sYXWTy4gQC2cEHcM9q3PAniDS7bUNdtriSVZrm8VrfYhYOVVQBuHTr3x0Na0mrJP+tDDG0ajqTSTseT3v/AB/3X/XaT/0I1FVm7/wCP+6/67Sf+hGqlYy3Z0U/hR//2Q==',
   'Governorpeta, Vijayawada', 16.5050, 80.6550, 'dog', 'vehicle_injury', 'urgent',
   'rescue_route',
   'Young female dog hit by speeding car — possible pelvic fracture, unable to stand, in visible pain',
   '["Approach extremely carefully — injured animals may bite out of fear.","Use a towel or blanket to gently lift and support the animal. Keep the spine as straight as possible.","If pelvic fracture is suspected, place the animal on a rigid board or flat surface.","Do NOT try to manipulate or straighten the legs.","Cover with a blanket to prevent shock.","Transport to veterinary hospital immediately — do NOT give food or water in case surgery is needed."]',
   4500,
   '["Arrived on scene within 20 min. Animal was lying on roadside, unable to stand. Used stretcher board to immobilize.","Transporting to VetCare Multi-Specialty Hospital. Estimated arrival in 10 min.","X-ray shows fractured pelvis — non-surgical management recommended. Cage rest for 4-6 weeks."]',
   '2026-03-08 18:10:00', '2026-03-08 19:30:00');

-- ─── Donations ─────────────────────────────────────────────────

INSERT INTO donation (case_id, donor_name, amount_inr, message, created_at)
VALUES
  (1, 'Priya Sharma',     500,  'Hope this helps Bhairava recover quickly! 🐾',                        '2026-01-16 10:00:00'),
  (1, 'Anand Rao',         300,  'Get well soon, little one',                                          '2026-01-17 14:30:00'),
  (1, 'Ravi Kumar',        200,  'Great work by the rescue team',                                      '2026-01-18 09:15:00'),
  (1, 'Anonymous',         700,  '',                                                                   '2026-01-20 11:00:00'),
  (2, 'Priya Sharma',      250,  'For the mange treatment',                                            '2026-02-11 16:45:00'),
  (2, 'Sunita Patel',      400,  'For medicated baths and medicines',                                  '2026-02-15 08:30:00'),
  (5, 'Anand Rao',         1000, 'Emergency pelvic fracture treatment — please save her!',             '2026-03-08 19:00:00');

-- ─── Adoption Applications ──────────────────────────────────────

INSERT INTO adoption_application (case_id, applicant_name, contact, reason, status, created_at)
VALUES
  (1, 'Mohan Das',     '91-9876543220', 'We have a fenced yard and are looking for a friendly dog to adopt. We saw Bhairava on the portal and fell in love.', 'approved',  '2026-02-01 09:00:00'),
  (1, 'Sita Reddy',    '91-9876543221', 'I want to give Bhairava a loving home. I have adopted strays before.',                                                      'rejected', '2026-02-02 10:00:00'),
  (2, 'Mohan Das',     '91-9876543220', 'We would love to adopt this dog once she recovers. We have experience with special needs animals.',                       'pending',  '2026-03-01 11:00:00');
