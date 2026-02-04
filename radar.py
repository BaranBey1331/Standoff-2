import math

def get_radar_coords(enemy_pos, local_pos, local_yaw, radar_center, scale=1.0):
    rel_x = enemy_pos.x - local_pos.x
    rel_y = enemy_pos.y - local_pos.y

    rad = math.radians(local_yaw)
    rot_x = rel_y * math.cos(rad) - rel_x * math.sin(rad)
    rot_y = rel_y * math.sin(rad) + rel_x * math.cos(rad)

    radar_x = radar_center[0] + rot_x * scale
    radar_y = radar_center[1] + rot_y * scale

    return (radar_x, radar_y)
