"use client";

import { useEffect, useState } from "react";
import { createClient } from "@/lib/supabase/client";
import { useRouter } from "next/navigation";

export default function ProfilePage() {
  const supabase = createClient();
  const router = useRouter();

  const [userEmail, setUserEmail] = useState("");
  const [userName, setUserName] = useState("");

  useEffect(() => {
    const getUser = async () => {
      const { data, error } = await supabase.auth.getUser();

      if (error || !data.user) {
        router.push("/login");
        return;
      }

      const email = data.user.email || "";
      setUserEmail(email);

      // Try to get name from metadata
      const name =
        data.user.user_metadata?.full_name ||
        data.user.user_metadata?.name ||
        email.split("@")[0]; // fallback

      setUserName(name);
    };

    getUser();
  }, []);

  const handleLogout = async () => {
    await supabase.auth.signOut();
    router.push("/login");
  };

  return (
    <div
      style={{
        height: "100vh",
        display: "flex",
        justifyContent: "center",
        alignItems: "center",
      }}
    >
      <div
        style={{
          background: "#1e293b",
          padding: 30,
          borderRadius: 10,
          color: "white",
          width: 350,
        }}
      >
        {/* Avatar */}
        <div
          style={{
            width: 60,
            height: 60,
            borderRadius: "50%",
            background: "#22c55e",
            display: "flex",
            alignItems: "center",
            justifyContent: "center",
            fontSize: 24,
            marginBottom: 15,
          }}
        >
          {userName ? userName[0].toUpperCase() : "U"}
        </div>

        {/* Name */}
        <h2>{userName || "Loading..."}</h2>

        {/* Email */}
        <p style={{ opacity: 0.7 }}>{userEmail}</p>

        {/* Role */}
        <p>Role: USER</p>

        {/* Logout */}
        <button
          onClick={handleLogout}
          style={{
            marginTop: 15,
            padding: "10px 20px",
            background: "#f59e0b",
            border: "none",
            borderRadius: 6,
            cursor: "pointer",
          }}
        >
          Logout
        </button>
      </div>
    </div>
  );
}