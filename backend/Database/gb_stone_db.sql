-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: localhost
-- Generation Time: Nov 27, 2025 at 09:03 AM
-- Server version: 10.4.28-MariaDB
-- PHP Version: 8.2.4

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `gb_stone_db`
--

-- --------------------------------------------------------

--
-- Table structure for table `doctors`
--

CREATE TABLE `doctors` (
  `id` int(11) NOT NULL,
  `email` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `full_name` varchar(255) NOT NULL,
  `hospital` varchar(255) DEFAULT NULL,
  `specialization` varchar(255) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `doctors`
--

INSERT INTO `doctors` (`id`, `email`, `password`, `full_name`, `hospital`, `specialization`, `created_at`) VALUES
(4, 'sarika@gmail.com', '$2y$10$iyx5GDUxoBBslVuOSKb/cOEm7cnaof3oso17gMaIbVz8LGKfth8Nu', 'Dr Sarika', 'RIMS', 'Radiologist', '2025-11-25 04:58:14'),
(5, 'tilak@gmail.com', '$2y$10$q39lKTNJby/RSwRVTNyJpOLaZQaGNw2it9MoQOiCSdkZ/rbG4VW5q', 'Dr Tilak', 'SIMS', 'Radiologist', '2025-11-25 05:12:39'),
(6, 'a1@gmail.com', '$2y$10$3RAmQKT4wYGMfJE9HP.hQOncUTpBqZJnvO4IhdAZLZRvg0sicjSjy', 'Dr Akhila', 'Saveetha', 'Radiologist', '2025-11-25 06:16:57'),
(7, 'sai@gmail.com', '$2y$10$.ZIPkRiS8fhmdxnoEJXOAe2pKi1zDEXjh9niTZuX8pQTY0IOynWeO', 'Dr Sai Kumar', 'Elite', 'Radiologist', '2025-11-26 03:27:30'),
(8, 'g@gmail.com', '$2y$10$5io57/qjNfg3bIcCdz6XEeaKiyEmvCZ/DrYKGccLm7A4EAUfaJIQm', 'G', 'S', 'Radiologist', '2025-11-26 03:56:10'),
(9, 'venky@gmail.com', '$2y$10$sx0hdktPmSqvHTg18SVqY.QZN7/io28j/6D6zTEj6wMu25HA7aXGC', 'Dr venky', 'SIMS', 'Radiologist', '2025-11-26 04:15:21'),
(10, 'akash@gmail.com', '$2y$10$BpXkPJNqpvSOSdrmhjCKO.GVz0g21dwg65VFoHm6y2sLZUCW54aRe', 'Dr Akash', 'Saveetha', 'Radiologist', '2025-11-26 04:27:10'),
(11, 'ram@gmail.com', '$2y$10$rEYs4g0AJZXXnjAZ2MnRSeJ7qet9/H.YkdDZmeUox50Knuv.FYsxO', 'Dr.Ram', 'SVIMS', 'Radiologist', '2025-11-26 04:47:51'),
(12, 'akhila@gmail.com', '$2y$10$D0dHlQWgi97iIv09SDUiZ.Dw1MEXv22Nk0y.qa6PmCGYuRMVGR3ZW', 'Dr Akhila', 'Saveetha', 'Radiologist', '2025-11-27 05:16:52');

-- --------------------------------------------------------

--
-- Table structure for table `scans`
--

CREATE TABLE `scans` (
  `id` int(11) NOT NULL,
  `doctor_id` int(11) NOT NULL,
  `patient_id` varchar(100) NOT NULL,
  `patient_name` varchar(255) NOT NULL,
  `scan_date` date NOT NULL,
  `is_positive` tinyint(1) DEFAULT 0,
  `stone_count` int(11) DEFAULT 0,
  `largest_stone_mm` decimal(5,2) DEFAULT 0.00,
  `ai_confidence` decimal(5,2) DEFAULT 0.00,
  `radiologist_text` text DEFAULT NULL,
  `annotated_image_url` varchar(500) DEFAULT NULL,
  `original_image_url` varchar(500) DEFAULT NULL,
  `patient_age` smallint(6) DEFAULT 0,
  `patient_gender` varchar(20) DEFAULT '',
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `scans`
--

INSERT INTO `scans` (`id`, `doctor_id`, `patient_id`, `patient_name`, `scan_date`, `is_positive`, `stone_count`, `largest_stone_mm`, `ai_confidence`, `radiologist_text`, `annotated_image_url`, `original_image_url`, `patient_age`, `patient_gender`, `created_at`) VALUES
(2, 5, 'P-1', 'Jyothika', '2025-11-25', 1, 4, 4.60, 56.01, 'Mild stone', '/Users/saill1/Library/Developer/CoreSimulator/Devices/D40286E5-2FA0-44F1-9E21-A17ED05FAD54/data/Containers/Data/Application/CC2F84F7-E83A-4BD2-9E7B-6AC157D33345/Documents/scan_P-1_1764047624_annotated.jpg', '/Users/saill1/Library/Developer/CoreSimulator/Devices/D40286E5-2FA0-44F1-9E21-A17ED05FAD54/data/Containers/Data/Application/CC2F84F7-E83A-4BD2-9E7B-6AC157D33345/Documents/scan_P-1_1764047624.jpg', 45, 'Female', '2025-11-25 05:13:44'),
(3, 5, 'P-2', 'Kamesh', '2025-11-25', 1, 1, 16.03, 71.23, 'Required surgery', '/Users/saill1/Library/Developer/CoreSimulator/Devices/D40286E5-2FA0-44F1-9E21-A17ED05FAD54/data/Containers/Data/Application/CC2F84F7-E83A-4BD2-9E7B-6AC157D33345/Documents/scan_P-2_1764047711_annotated.jpg', '/Users/saill1/Library/Developer/CoreSimulator/Devices/D40286E5-2FA0-44F1-9E21-A17ED05FAD54/data/Containers/Data/Application/CC2F84F7-E83A-4BD2-9E7B-6AC157D33345/Documents/scan_P-2_1764047711.jpg', 0, 'Male', '2025-11-25 05:15:11'),
(41, 5, 'P-6', 'Grishma', '2025-11-25', 1, 1, 3.77, 29.79, 'Mild', '/Users/saill1/Library/Developer/CoreSimulator/Devices/D40286E5-2FA0-44F1-9E21-A17ED05FAD54/data/Containers/Data/Application/A6644602-A231-464C-80D1-12F1D5AE4B0B/Documents/scan_P-6_1764050370_annotated.jpg', '/Users/saill1/Library/Developer/CoreSimulator/Devices/D40286E5-2FA0-44F1-9E21-A17ED05FAD54/data/Containers/Data/Application/A6644602-A231-464C-80D1-12F1D5AE4B0B/Documents/scan_P-6_1764050370.jpg', 0, 'Male', '2025-11-25 06:07:20'),
(42, 5, 'P-4', 'Srinu', '2025-11-25', 1, 4, 4.60, 56.01, 'Mild ', '/Users/saill1/Library/Developer/CoreSimulator/Devices/D40286E5-2FA0-44F1-9E21-A17ED05FAD54/data/Containers/Data/Application/A6644602-A231-464C-80D1-12F1D5AE4B0B/Documents/scan_P-4_1764050230_annotated.jpg', '/Users/saill1/Library/Developer/CoreSimulator/Devices/D40286E5-2FA0-44F1-9E21-A17ED05FAD54/data/Containers/Data/Application/A6644602-A231-464C-80D1-12F1D5AE4B0B/Documents/scan_P-4_1764050230.jpg', 0, 'Male', '2025-11-25 06:07:20'),
(43, 5, 'P-5', 'Babitha', '2025-11-25', 1, 1, 17.36, 83.35, 'Required surgery', '/Users/saill1/Library/Developer/CoreSimulator/Devices/D40286E5-2FA0-44F1-9E21-A17ED05FAD54/data/Containers/Data/Application/A6644602-A231-464C-80D1-12F1D5AE4B0B/Documents/scan_P-5_1764050284_annotated.jpg', '/Users/saill1/Library/Developer/CoreSimulator/Devices/D40286E5-2FA0-44F1-9E21-A17ED05FAD54/data/Containers/Data/Application/A6644602-A231-464C-80D1-12F1D5AE4B0B/Documents/scan_P-5_1764050284.jpg', 0, 'Male', '2025-11-25 06:07:20'),
(44, 5, 'P-53', 'Venky', '2025-11-25', 1, 1, 15.77, 89.34, 'Require surgery', '/Users/saill1/Library/Developer/CoreSimulator/Devices/D40286E5-2FA0-44F1-9E21-A17ED05FAD54/data/Containers/Data/Application/A6644602-A231-464C-80D1-12F1D5AE4B0B/Documents/scan_P-53_1764050701_annotated.jpg', '/Users/saill1/Library/Developer/CoreSimulator/Devices/D40286E5-2FA0-44F1-9E21-A17ED05FAD54/data/Containers/Data/Application/A6644602-A231-464C-80D1-12F1D5AE4B0B/Documents/scan_P-53_1764050701.jpg', 60, 'Female', '2025-11-25 06:07:20'),
(45, 6, 'P-3', 'Dhanush', '2025-11-25', 1, 1, 16.03, 71.23, 'Large', '/Users/saill1/Library/Developer/CoreSimulator/Devices/D40286E5-2FA0-44F1-9E21-A17ED05FAD54/data/Containers/Data/Application/EC57F0DF-4036-4A5E-9DB4-8FECB298A1ED/Documents/scan_P-3_1764056671_annotated.jpg', '/Users/saill1/Library/Developer/CoreSimulator/Devices/D40286E5-2FA0-44F1-9E21-A17ED05FAD54/data/Containers/Data/Application/EC57F0DF-4036-4A5E-9DB4-8FECB298A1ED/Documents/scan_P-3_1764056671.jpg', 0, 'Male', '2025-11-25 07:44:31'),
(46, 6, 'P-45', 'Akhil', '2025-11-25', 1, 1, 3.85, 35.67, 'Mild', '/Users/saill1/Library/Developer/CoreSimulator/Devices/D40286E5-2FA0-44F1-9E21-A17ED05FAD54/data/Containers/Data/Application/EC57F0DF-4036-4A5E-9DB4-8FECB298A1ED/Documents/scan_P-45_1764056717_annotated.jpg', '/Users/saill1/Library/Developer/CoreSimulator/Devices/D40286E5-2FA0-44F1-9E21-A17ED05FAD54/data/Containers/Data/Application/EC57F0DF-4036-4A5E-9DB4-8FECB298A1ED/Documents/scan_P-45_1764056717.jpg', 25, 'Male', '2025-11-25 07:45:17'),
(47, 6, 'P-234', 'Siri', '2025-11-25', 1, 3, 4.28, 73.84, 'Mild', '/Users/saill1/Library/Developer/CoreSimulator/Devices/D40286E5-2FA0-44F1-9E21-A17ED05FAD54/data/Containers/Data/Application/EC57F0DF-4036-4A5E-9DB4-8FECB298A1ED/Documents/scan_P-234_1764056762_annotated.jpg', '/Users/saill1/Library/Developer/CoreSimulator/Devices/D40286E5-2FA0-44F1-9E21-A17ED05FAD54/data/Containers/Data/Application/EC57F0DF-4036-4A5E-9DB4-8FECB298A1ED/Documents/scan_P-234_1764056762.jpg', 0, 'Male', '2025-11-25 07:46:02'),
(48, 6, 'P-111', 'Sanvi', '2025-11-25', 1, 1, 17.36, 83.35, 'Large', '/Users/saill1/Library/Developer/CoreSimulator/Devices/D40286E5-2FA0-44F1-9E21-A17ED05FAD54/data/Containers/Data/Application/EC57F0DF-4036-4A5E-9DB4-8FECB298A1ED/Documents/scan_P-111_1764056830_annotated.jpg', '/Users/saill1/Library/Developer/CoreSimulator/Devices/D40286E5-2FA0-44F1-9E21-A17ED05FAD54/data/Containers/Data/Application/EC57F0DF-4036-4A5E-9DB4-8FECB298A1ED/Documents/scan_P-111_1764056830.jpg', 40, 'Female', '2025-11-25 07:47:11'),
(49, 6, 'P-567', 'Xyz', '2025-11-25', 1, 1, 14.33, 42.26, 'Surgery required', '/Users/saill1/Library/Developer/CoreSimulator/Devices/D40286E5-2FA0-44F1-9E21-A17ED05FAD54/data/Containers/Data/Application/EC57F0DF-4036-4A5E-9DB4-8FECB298A1ED/Documents/scan_P-567_1764056879_annotated.jpg', '/Users/saill1/Library/Developer/CoreSimulator/Devices/D40286E5-2FA0-44F1-9E21-A17ED05FAD54/data/Containers/Data/Application/EC57F0DF-4036-4A5E-9DB4-8FECB298A1ED/Documents/scan_P-567_1764056879.jpg', 0, 'Male', '2025-11-25 07:47:59'),
(50, 6, 'P-222', 'Asfdgc', '2025-11-25', 1, 4, 4.60, 56.01, 'Mild', '/Users/saill1/Library/Developer/CoreSimulator/Devices/D40286E5-2FA0-44F1-9E21-A17ED05FAD54/data/Containers/Data/Application/EC57F0DF-4036-4A5E-9DB4-8FECB298A1ED/Documents/scan_P-222_1764056924_annotated.jpg', '/Users/saill1/Library/Developer/CoreSimulator/Devices/D40286E5-2FA0-44F1-9E21-A17ED05FAD54/data/Containers/Data/Application/EC57F0DF-4036-4A5E-9DB4-8FECB298A1ED/Documents/scan_P-222_1764056924.jpg', 0, 'Male', '2025-11-25 07:48:44'),
(51, 6, 'P-12345', 'Priyanka', '2025-11-25', 1, 1, 17.36, 83.35, 'Surgery required', '/Users/saill1/Library/Developer/CoreSimulator/Devices/D40286E5-2FA0-44F1-9E21-A17ED05FAD54/data/Containers/Data/Application/EC57F0DF-4036-4A5E-9DB4-8FECB298A1ED/Documents/scan_P-12345_1764057466_annotated.jpg', '/Users/saill1/Library/Developer/CoreSimulator/Devices/D40286E5-2FA0-44F1-9E21-A17ED05FAD54/data/Containers/Data/Application/EC57F0DF-4036-4A5E-9DB4-8FECB298A1ED/Documents/scan_P-12345_1764057466.jpg', 35, 'Female', '2025-11-25 07:57:46'),
(52, 6, 'P-432', 'Krithi', '2025-11-25', 1, 3, 3.25, 55.46, 'Mild', '/Users/saill1/Library/Developer/CoreSimulator/Devices/D40286E5-2FA0-44F1-9E21-A17ED05FAD54/data/Containers/Data/Application/40C26E50-DF8E-42F5-B258-178776AD76C1/Documents/scan_P-432_1764062699_annotated.jpg', '/Users/saill1/Library/Developer/CoreSimulator/Devices/D40286E5-2FA0-44F1-9E21-A17ED05FAD54/data/Containers/Data/Application/40C26E50-DF8E-42F5-B258-178776AD76C1/Documents/scan_P-432_1764062699.jpg', 0, 'Male', '2025-11-25 09:24:59'),
(53, 5, 'P-1000', 'Kishore', '2025-11-25', 1, 3, 3.25, 55.46, 'Mild calculi', '/Users/saill1/Library/Developer/CoreSimulator/Devices/D40286E5-2FA0-44F1-9E21-A17ED05FAD54/data/Containers/Data/Application/76AD0F8D-DEEC-4F49-9CDB-A32763B5237C/Documents/scan_P-1000_1764065401_annotated.jpg', '/Users/saill1/Library/Developer/CoreSimulator/Devices/D40286E5-2FA0-44F1-9E21-A17ED05FAD54/data/Containers/Data/Application/76AD0F8D-DEEC-4F49-9CDB-A32763B5237C/Documents/scan_P-1000_1764065401.jpg', 0, 'Male', '2025-11-25 10:10:02'),
(54, 5, 'P-1001', 'Naveen', '2025-11-25', 1, 1, 15.46, 81.43, 'Surgery required', '/Users/saill1/Library/Developer/CoreSimulator/Devices/D40286E5-2FA0-44F1-9E21-A17ED05FAD54/data/Containers/Data/Application/76AD0F8D-DEEC-4F49-9CDB-A32763B5237C/Documents/scan_P-1001_1764065453_annotated.jpg', '/Users/saill1/Library/Developer/CoreSimulator/Devices/D40286E5-2FA0-44F1-9E21-A17ED05FAD54/data/Containers/Data/Application/76AD0F8D-DEEC-4F49-9CDB-A32763B5237C/Documents/scan_P-1001_1764065453.jpg', 0, 'Male', '2025-11-25 10:10:53'),
(55, 7, 'P-09', 'Ayesha', '2025-11-26', 1, 1, 17.36, 83.35, 'Surgery required immediatey', '/Users/saill1/Library/Developer/CoreSimulator/Devices/D40286E5-2FA0-44F1-9E21-A17ED05FAD54/data/Containers/Data/Application/7318A9F4-60CB-4AD9-A0BA-C25EE773C891/Documents/scan_P-09_1764127737_annotated.jpg', '/Users/saill1/Library/Developer/CoreSimulator/Devices/D40286E5-2FA0-44F1-9E21-A17ED05FAD54/data/Containers/Data/Application/7318A9F4-60CB-4AD9-A0BA-C25EE773C891/Documents/scan_P-09_1764127737.jpg', 45, 'Female', '2025-11-26 03:28:57'),
(56, 7, 'P-098', 'Kalyan', '2025-11-26', 1, 3, 4.28, 73.84, 'Mild calculi', '/Users/saill1/Library/Developer/CoreSimulator/Devices/D40286E5-2FA0-44F1-9E21-A17ED05FAD54/data/Containers/Data/Application/7318A9F4-60CB-4AD9-A0BA-C25EE773C891/Documents/scan_P-098_1764127839_annotated.jpg', '/Users/saill1/Library/Developer/CoreSimulator/Devices/D40286E5-2FA0-44F1-9E21-A17ED05FAD54/data/Containers/Data/Application/7318A9F4-60CB-4AD9-A0BA-C25EE773C891/Documents/scan_P-098_1764127839.jpg', 0, 'Male', '2025-11-26 03:30:39'),
(57, 7, 'P-0987', 'Preethi', '2025-11-26', 1, 1, 3.77, 29.79, 'Stone is mild can be cured quickly', '/Users/saill1/Library/Developer/CoreSimulator/Devices/D40286E5-2FA0-44F1-9E21-A17ED05FAD54/data/Containers/Data/Application/7318A9F4-60CB-4AD9-A0BA-C25EE773C891/Documents/scan_P-0987_1764128034_annotated.jpg', '/Users/saill1/Library/Developer/CoreSimulator/Devices/D40286E5-2FA0-44F1-9E21-A17ED05FAD54/data/Containers/Data/Application/7318A9F4-60CB-4AD9-A0BA-C25EE773C891/Documents/scan_P-0987_1764128034.jpg', 0, 'Male', '2025-11-26 03:33:54'),
(58, 7, 'P-98', 'Siddhu', '2025-11-26', 1, 1, 10.96, 77.37, 'Moderate', '/Users/saill1/Library/Developer/CoreSimulator/Devices/D40286E5-2FA0-44F1-9E21-A17ED05FAD54/data/Containers/Data/Application/7318A9F4-60CB-4AD9-A0BA-C25EE773C891/Documents/scan_P-98_1764128114_annotated.jpg', '/Users/saill1/Library/Developer/CoreSimulator/Devices/D40286E5-2FA0-44F1-9E21-A17ED05FAD54/data/Containers/Data/Application/7318A9F4-60CB-4AD9-A0BA-C25EE773C891/Documents/scan_P-98_1764128114.jpg', 0, 'Male', '2025-11-26 03:35:14'),
(59, 8, 'P-00', 'S', '2025-11-26', 1, 1, 17.36, 83.35, 'Required surgery', '/Users/saill1/Library/Developer/CoreSimulator/Devices/D40286E5-2FA0-44F1-9E21-A17ED05FAD54/data/Containers/Data/Application/928AAEBF-6D39-4C97-993B-4ECDB0D73354/Documents/scan_P-00_1764129425_annotated.jpg', '/Users/saill1/Library/Developer/CoreSimulator/Devices/D40286E5-2FA0-44F1-9E21-A17ED05FAD54/data/Containers/Data/Application/928AAEBF-6D39-4C97-993B-4ECDB0D73354/Documents/scan_P-00_1764129425.jpg', 0, 'Prefer not to say', '2025-11-26 03:57:05'),
(60, 8, 'P-87', 'Goff', '2025-11-26', 1, 1, 14.33, 42.26, NULL, '/Users/saill1/Library/Developer/CoreSimulator/Devices/D40286E5-2FA0-44F1-9E21-A17ED05FAD54/data/Containers/Data/Application/928AAEBF-6D39-4C97-993B-4ECDB0D73354/Documents/scan_P-87_1764129504_annotated.jpg', '/Users/saill1/Library/Developer/CoreSimulator/Devices/D40286E5-2FA0-44F1-9E21-A17ED05FAD54/data/Containers/Data/Application/928AAEBF-6D39-4C97-993B-4ECDB0D73354/Documents/scan_P-87_1764129504.jpg', 0, 'Prefer not to say', '2025-11-26 03:58:24'),
(61, 8, 'PP', 'Bhuj', '2025-11-26', 1, 1, 14.33, 42.26, 'Guy', '/Users/saill1/Library/Developer/CoreSimulator/Devices/D40286E5-2FA0-44F1-9E21-A17ED05FAD54/data/Containers/Data/Application/928AAEBF-6D39-4C97-993B-4ECDB0D73354/Documents/scan_PP_1764129555_annotated.jpg', '/Users/saill1/Library/Developer/CoreSimulator/Devices/D40286E5-2FA0-44F1-9E21-A17ED05FAD54/data/Containers/Data/Application/928AAEBF-6D39-4C97-993B-4ECDB0D73354/Documents/scan_PP_1764129555.jpg', 0, 'Prefer not to say', '2025-11-26 03:59:15'),
(62, 8, 'P-45678', 'V', '2025-11-26', 0, 0, 0.00, 0.00, 'Ok', '/Users/saill1/Library/Developer/CoreSimulator/Devices/D40286E5-2FA0-44F1-9E21-A17ED05FAD54/data/Containers/Data/Application/928AAEBF-6D39-4C97-993B-4ECDB0D73354/Documents/scan_P-45678_1764130278_annotated.jpg', '/Users/saill1/Library/Developer/CoreSimulator/Devices/D40286E5-2FA0-44F1-9E21-A17ED05FAD54/data/Containers/Data/Application/928AAEBF-6D39-4C97-993B-4ECDB0D73354/Documents/scan_P-45678_1764130278.jpg', 67, 'Male', '2025-11-26 04:11:19'),
(63, 9, 'P-1', 'Jyothika', '2025-11-26', 1, 1, 15.77, 89.34, 'Required to do surgery', '/Users/saill1/Library/Developer/CoreSimulator/Devices/D40286E5-2FA0-44F1-9E21-A17ED05FAD54/data/Containers/Data/Application/928AAEBF-6D39-4C97-993B-4ECDB0D73354/Documents/scan_P-1_1764130568_annotated.jpg', '/Users/saill1/Library/Developer/CoreSimulator/Devices/D40286E5-2FA0-44F1-9E21-A17ED05FAD54/data/Containers/Data/Application/928AAEBF-6D39-4C97-993B-4ECDB0D73354/Documents/scan_P-1_1764130568.jpg', 30, 'Female', '2025-11-26 04:16:09'),
(64, 9, 'P-2', 'Priya', '2025-11-26', 0, 0, 0.00, 0.00, 'No calculi ', '/Users/saill1/Library/Developer/CoreSimulator/Devices/D40286E5-2FA0-44F1-9E21-A17ED05FAD54/data/Containers/Data/Application/928AAEBF-6D39-4C97-993B-4ECDB0D73354/Documents/scan_P-2_1764130632_annotated.jpg', '/Users/saill1/Library/Developer/CoreSimulator/Devices/D40286E5-2FA0-44F1-9E21-A17ED05FAD54/data/Containers/Data/Application/928AAEBF-6D39-4C97-993B-4ECDB0D73354/Documents/scan_P-2_1764130632.jpg', 0, 'Prefer not to say', '2025-11-26 04:17:13'),
(65, 10, 'P-22', 'Akhila', '2025-11-26', 1, 4, 4.60, 56.01, 'Mild stones', '/Users/saill1/Library/Developer/CoreSimulator/Devices/D40286E5-2FA0-44F1-9E21-A17ED05FAD54/data/Containers/Data/Application/AF33EBA1-49B7-4579-9F85-D6A1C7B1D4F0/Documents/scan_P-22_1764131276_annotated.jpg', '/Users/saill1/Library/Developer/CoreSimulator/Devices/D40286E5-2FA0-44F1-9E21-A17ED05FAD54/data/Containers/Data/Application/AF33EBA1-49B7-4579-9F85-D6A1C7B1D4F0/Documents/scan_P-22_1764131276.jpg', 20, 'Female', '2025-11-26 04:27:56'),
(66, 10, 'P-5432', 'Venky', '2025-11-26', 0, 0, 0.00, 0.00, 'No calculi', '/Users/saill1/Library/Developer/CoreSimulator/Devices/D40286E5-2FA0-44F1-9E21-A17ED05FAD54/data/Containers/Data/Application/AF33EBA1-49B7-4579-9F85-D6A1C7B1D4F0/Documents/scan_P-5432_1764131326_annotated.jpg', '/Users/saill1/Library/Developer/CoreSimulator/Devices/D40286E5-2FA0-44F1-9E21-A17ED05FAD54/data/Containers/Data/Application/AF33EBA1-49B7-4579-9F85-D6A1C7B1D4F0/Documents/scan_P-5432_1764131326.jpg', 0, 'Prefer not to say', '2025-11-26 04:28:46'),
(67, 11, 'P-22', 'Siri', '2025-11-26', 1, 1, 15.77, 89.34, 'Required surgery', '/Users/saill1/Library/Developer/CoreSimulator/Devices/D40286E5-2FA0-44F1-9E21-A17ED05FAD54/data/Containers/Data/Application/396F1C0C-594C-4D07-8585-7CBDA81F1B98/Documents/scan_P-22_1764132515_annotated.jpg', '/Users/saill1/Library/Developer/CoreSimulator/Devices/D40286E5-2FA0-44F1-9E21-A17ED05FAD54/data/Containers/Data/Application/396F1C0C-594C-4D07-8585-7CBDA81F1B98/Documents/scan_P-22_1764132515.jpg', 45, 'Female', '2025-11-26 04:48:35'),
(68, 11, 'P-2', 'Shiva', '2025-11-26', 0, 0, 0.00, 0.00, 'No calculi', '/Users/saill1/Library/Developer/CoreSimulator/Devices/D40286E5-2FA0-44F1-9E21-A17ED05FAD54/data/Containers/Data/Application/396F1C0C-594C-4D07-8585-7CBDA81F1B98/Documents/scan_P-2_1764132568_annotated.jpg', '/Users/saill1/Library/Developer/CoreSimulator/Devices/D40286E5-2FA0-44F1-9E21-A17ED05FAD54/data/Containers/Data/Application/396F1C0C-594C-4D07-8585-7CBDA81F1B98/Documents/scan_P-2_1764132568.jpg', 0, 'Prefer not to say', '2025-11-26 04:49:28'),
(69, 11, 'P-87', 'Jyo', '2025-11-26', 1, 1, 15.77, 89.34, NULL, '/Users/saill1/Library/Developer/CoreSimulator/Devices/D40286E5-2FA0-44F1-9E21-A17ED05FAD54/data/Containers/Data/Application/E6C9BD68-85D4-483F-95F8-3C4C15115790/Documents/scan_P-87_1764136106_annotated.jpg', '/Users/saill1/Library/Developer/CoreSimulator/Devices/D40286E5-2FA0-44F1-9E21-A17ED05FAD54/data/Containers/Data/Application/E6C9BD68-85D4-483F-95F8-3C4C15115790/Documents/scan_P-87_1764136106.jpg', 0, 'Prefer not to say', '2025-11-26 05:48:26'),
(70, 11, 'TEST', 'Test test', '2025-11-26', 1, 3, 4.28, 73.84, NULL, '/Users/saill1/Library/Developer/CoreSimulator/Devices/D40286E5-2FA0-44F1-9E21-A17ED05FAD54/data/Containers/Data/Application/EDA9C4CA-1146-4180-ABDC-4CC4482F4F31/Documents/scan_TEST_1764142849_annotated.jpg', '/Users/saill1/Library/Developer/CoreSimulator/Devices/D40286E5-2FA0-44F1-9E21-A17ED05FAD54/data/Containers/Data/Application/EDA9C4CA-1146-4180-ABDC-4CC4482F4F31/Documents/scan_TEST_1764142849.jpg', 0, 'Prefer not to say', '2025-11-26 07:40:50'),
(71, 11, 'NEW', 'NewNew', '2025-11-26', 1, 2, 4.42, 70.36, NULL, '/Users/saill1/Library/Developer/CoreSimulator/Devices/D40286E5-2FA0-44F1-9E21-A17ED05FAD54/data/Containers/Data/Application/EDA9C4CA-1146-4180-ABDC-4CC4482F4F31/Documents/scan_NEW_1764142931_annotated.jpg', '/Users/saill1/Library/Developer/CoreSimulator/Devices/D40286E5-2FA0-44F1-9E21-A17ED05FAD54/data/Containers/Data/Application/EDA9C4CA-1146-4180-ABDC-4CC4482F4F31/Documents/scan_NEW_1764142931.jpg', 0, 'Prefer not to say', '2025-11-26 07:42:11'),
(72, 8, 'PATIENT-1', 'Babitha', '2025-11-26', 1, 1, 3.77, 29.79, 'Mild', '/Users/saill1/Library/Developer/CoreSimulator/Devices/D40286E5-2FA0-44F1-9E21-A17ED05FAD54/data/Containers/Data/Application/FBE5066B-6088-4952-8E3A-E39EDDBE46F6/Documents/scan_PATIENT-1_1764145138_annotated.jpg', '/Users/saill1/Library/Developer/CoreSimulator/Devices/D40286E5-2FA0-44F1-9E21-A17ED05FAD54/data/Containers/Data/Application/FBE5066B-6088-4952-8E3A-E39EDDBE46F6/Documents/scan_PATIENT-1_1764145138.jpg', 0, 'Prefer not to say', '2025-11-26 08:18:58'),
(73, 8, 'P-1', 'Krish', '2025-11-26', 1, 1, 15.77, 89.34, NULL, '/Users/saill1/Library/Developer/CoreSimulator/Devices/D40286E5-2FA0-44F1-9E21-A17ED05FAD54/data/Containers/Data/Application/0714DB58-173B-49A0-B94A-7466D4A3AB2C/Documents/scan_P-1_1764148450_annotated.jpg', '/Users/saill1/Library/Developer/CoreSimulator/Devices/D40286E5-2FA0-44F1-9E21-A17ED05FAD54/data/Containers/Data/Application/0714DB58-173B-49A0-B94A-7466D4A3AB2C/Documents/scan_P-1_1764148450.jpg', 0, 'Prefer not to say', '2025-11-26 09:14:10'),
(74, 8, 'P-2', 'Rishi', '2025-11-26', 1, 1, 15.77, 89.34, NULL, '/Users/saill1/Library/Developer/CoreSimulator/Devices/D40286E5-2FA0-44F1-9E21-A17ED05FAD54/data/Containers/Data/Application/0714DB58-173B-49A0-B94A-7466D4A3AB2C/Documents/scan_P-2_1764148631_annotated.jpg', '/Users/saill1/Library/Developer/CoreSimulator/Devices/D40286E5-2FA0-44F1-9E21-A17ED05FAD54/data/Containers/Data/Application/0714DB58-173B-49A0-B94A-7466D4A3AB2C/Documents/scan_P-2_1764148631.jpg', 0, 'Prefer not to say', '2025-11-26 09:17:11'),
(75, 8, 'Dggf', 'Ggggjf', '2025-11-26', 1, 1, 15.77, 89.34, NULL, '/Users/saill1/Library/Developer/CoreSimulator/Devices/D40286E5-2FA0-44F1-9E21-A17ED05FAD54/data/Containers/Data/Application/EEBEF4FA-8DC3-4DC8-9534-D86C83146CA6/Documents/scan_Dggf_1764149812_annotated.jpg', '/Users/saill1/Library/Developer/CoreSimulator/Devices/D40286E5-2FA0-44F1-9E21-A17ED05FAD54/data/Containers/Data/Application/EEBEF4FA-8DC3-4DC8-9534-D86C83146CA6/Documents/scan_Dggf_1764149812.jpg', 0, 'Prefer not to say', '2025-11-26 09:36:52'),
(76, 12, 'P-1', 'Riya', '2025-11-27', 1, 1, 17.36, 83.35, 'Surgery', '/Users/saill1/Library/Developer/CoreSimulator/Devices/D40286E5-2FA0-44F1-9E21-A17ED05FAD54/data/Containers/Data/Application/C6B6B4C4-29BB-44A8-A923-10647784DEF3/Documents/scan_P-1_1764222287_annotated.jpg', '/Users/saill1/Library/Developer/CoreSimulator/Devices/D40286E5-2FA0-44F1-9E21-A17ED05FAD54/data/Containers/Data/Application/C6B6B4C4-29BB-44A8-A923-10647784DEF3/Documents/scan_P-1_1764222287.jpg', 0, 'Prefer not to say', '2025-11-27 05:44:48');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `doctors`
--
ALTER TABLE `doctors`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `email` (`email`),
  ADD KEY `idx_email` (`email`);

--
-- Indexes for table `scans`
--
ALTER TABLE `scans`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idx_doctor_id` (`doctor_id`),
  ADD KEY `idx_patient_id` (`patient_id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `doctors`
--
ALTER TABLE `doctors`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=13;

--
-- AUTO_INCREMENT for table `scans`
--
ALTER TABLE `scans`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=77;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `scans`
--
ALTER TABLE `scans`
  ADD CONSTRAINT `scans_ibfk_1` FOREIGN KEY (`doctor_id`) REFERENCES `doctors` (`id`) ON DELETE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
