-- Fetch Punjab state ID once
SET @state_id = (SELECT id FROM master_lookup 
                  WHERE type = 'STATE' AND value = 'Punjab' LIMIT 1);

-- Insert all Punjab CITYs using the variable
INSERT INTO master_lookup (type, code, value, status, parent_id) VALUES
('CITY', 'PB-AMR', 'Amritsar', 'ACTIVE', @state_id),
('CITY', 'PB-BRN', 'Barnala', 'ACTIVE', @state_id),
('CITY', 'PB-BTP', 'Bathinda', 'ACTIVE', @state_id),
('CITY', 'PB-FRP', 'Faridkot', 'ACTIVE', @state_id),
('CITY', 'PB-FTG', 'Fatehgarh Sahib', 'ACTIVE', @state_id),
('CITY', 'PB-FZP', 'Fazilka', 'ACTIVE', @state_id),
('CITY', 'PB-FZR', 'Ferozepur', 'ACTIVE', @state_id),
('CITY', 'PB-GRD', 'Gurdaspur', 'ACTIVE', @state_id),
('CITY', 'PB-HSP', 'Hoshiarpur', 'ACTIVE', @state_id),
('CITY', 'PB-JLP', 'Jalandhar', 'ACTIVE', @state_id),
('CITY', 'PB-KPT', 'Kapurthala', 'ACTIVE', @state_id),
('CITY', 'PB-LDH', 'Ludhiana', 'ACTIVE', @state_id),
('CITY', 'PB-MGR', 'Mansa', 'ACTIVE', @state_id),
('CITY', 'PB-MRT', 'Moga', 'ACTIVE', @state_id),
('CITY', 'PB-MKS', 'Muktsar', 'ACTIVE', @state_id),
('CITY', 'PB-PTH', 'Pathankot', 'ACTIVE', @state_id),
('CITY', 'PB-PDP', 'Patiala', 'ACTIVE', @state_id),
('CITY', 'PB-RPR', 'Rupnagar (Ropar)', 'ACTIVE', @state_id),
('CITY', 'PB-SBS', 'S.A.S. Nagar (Mohali)', 'ACTIVE', @state_id),
('CITY', 'PB-SBJ', 'Sangrur', 'ACTIVE', @state_id),
('CITY', 'PB-SHG', 'Shaheed Bhagat Singh Nagar (Nawanshahr)', 'ACTIVE', @state_id),
('CITY', 'PB-TRN', 'Tarn Taran', 'ACTIVE', @state_id);


-- Fetch Haryana state ID once
SET @state_id = (SELECT id FROM master_lookup 
                   WHERE type = 'STATE' AND value = 'Haryana' LIMIT 1);

-- Insert all Haryana CITYs
INSERT INTO master_lookup (type, code, value, status, parent_id) VALUES
('CITY', 'HR-AMG', 'Ambala', 'ACTIVE', @state_id),
('CITY', 'HR-BHI', 'Bhiwani', 'ACTIVE', @state_id),
('CITY', 'HR-CTK', 'Charkhi Dadri', 'ACTIVE', @state_id),
('CITY', 'HR-FBD', 'Faridabad', 'ACTIVE', @state_id),
('CITY', 'HR-FTG', 'Fatehabad', 'ACTIVE', @state_id),
('CITY', 'HR-GGN', 'Gurugram', 'ACTIVE', @state_id),
('CITY', 'HR-HSR', 'Hisar', 'ACTIVE', @state_id),
('CITY', 'HR-JHJ', 'Jhajjar', 'ACTIVE', @state_id),
('CITY', 'HR-JIN', 'Jind', 'ACTIVE', @state_id),
('CITY', 'HR-KRL', 'Kaithal', 'ACTIVE', @state_id),
('CITY', 'HR-KNL', 'Karnal', 'ACTIVE', @state_id),
('CITY', 'HR-KTH', 'Kurukshetra', 'ACTIVE', @state_id),
('CITY', 'HR-MW',  'Mahendragarh', 'ACTIVE', @state_id),
('CITY', 'HR-NNL', 'Nuh', 'ACTIVE', @state_id),
('CITY', 'HR-PAL', 'Palwal', 'ACTIVE', @state_id),
('CITY', 'HR-PNP', 'Panchkula', 'ACTIVE', @state_id),
('CITY', 'HR-PWL', 'Panipat', 'ACTIVE', @state_id),
('CITY', 'HR-RWR', 'Rewari', 'ACTIVE', @state_id),
('CITY', 'HR-RTK', 'Rohtak', 'ACTIVE', @state_id),
('CITY', 'HR-SNM', 'Sirsa', 'ACTIVE', @state_id),
('CITY', 'HR-SNP', 'Sonipat', 'ACTIVE', @state_id),
('CITY', 'HR-YNL', 'Yamunanagar', 'ACTIVE', @state_id);
