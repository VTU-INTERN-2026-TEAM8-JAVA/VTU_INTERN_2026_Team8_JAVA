"use client";

import { useState, useEffect, useRef } from "react";
import { useRouter } from "next/navigation";
import { createClient } from "@supabase/supabase-js";
import { Bell, User } from "lucide-react";

const supabase = createClient(
  process.env.NEXT_PUBLIC_SUPABASE_URL!,
  process.env.NEXT_PUBLIC_SUPABASE_ANON_KEY!
);

export default function Header() {
  const router = useRouter();

  const [showNotifications, setShowNotifications] = useState(false);
  const [showProfile, setShowProfile] = useState(false);

  const notificationRef = useRef<HTMLDivElement>(null);
  const profileRef = useRef<HTMLDivElement>(null);

  const notifications = [
    "SIP invested successfully",
    "Portfolio updated",
    "Reminder: Add funds",
  ];

  // ✅ Click outside close
  useEffect(() => {
    function handleClickOutside(event: MouseEvent) {
      if (
        notificationRef.current &&
        !notificationRef.current.contains(event.target as Node)
      ) {
        setShowNotifications(false);
      }

      if (
        profileRef.current &&
        !profileRef.current.contains(event.target as Node)
      ) {
        setShowProfile(false);
      }
    }

    document.addEventListener("mousedown", handleClickOutside);

    return () => {
      document.removeEventListener("mousedown", handleClickOutside);
    };
  }, []);

  const handleLogout = async () => {
    await supabase.auth.signOut();
    router.push("/login");
  };

  return (
    <div
      style={{
        display: "flex",
        justifyContent: "space-between",
        padding: 20,
        background: "#1a1a1a",
        color: "white",
        position: "relative",
      }}
    >
      <h2>WealthWise</h2>

      <div style={{ display: "flex", gap: 20 }}>
        
        {/* 🔔 Bell */}
        <div
          onClick={() => {
            setShowNotifications(!showNotifications);
            setShowProfile(false);
          }}
          style={{ cursor: "pointer", position: "relative" }}
        >
          <Bell size={20} />
          <span
            style={{
              position: "absolute",
              top: -5,
              right: -5,
              background: "red",
              borderRadius: "50%",
              fontSize: 10,
              padding: "2px 6px",
            }}
          >
            {notifications.length}
          </span>
        </div>

        {/* 👤 Profile */}
        <div
          onClick={() => {
            setShowProfile(!showProfile);
            setShowNotifications(false);
          }}
          style={{ cursor: "pointer" }}
        >
          <User size={20} />
        </div>
      </div>

      {/* 🔔 Notification Dropdown */}
      {showNotifications && (
        <div
          ref={notificationRef}
          style={{
            position: "absolute",
            top: 60,
            right: 60,
            background: "#2a2a2a",
            padding: 10,
            borderRadius: 10,
            zIndex: 1000,
          }}
        >
          {notifications.map((n, i) => (
            <p key={i}>{n}</p>
          ))}
        </div>
      )}

      {/* 👤 Profile Dropdown */}
      {showProfile && (
        <div
          ref={profileRef}
          style={{
            position: "absolute",
            top: 60,
            right: 10,
            background: "#2a2a2a",
            padding: 10,
            borderRadius: 10,
            zIndex: 1000,
          }}
        >
          <p
            onClick={() => {
              setShowProfile(false);
              router.push("/profile");
            }}
            style={{ cursor: "pointer" }}
          >
            Profile
          </p>

          <p
            onClick={handleLogout}
            style={{ cursor: "pointer", color: "red" }}
          >
            Logout
          </p>
        </div>
      )}
    </div>
  );
}